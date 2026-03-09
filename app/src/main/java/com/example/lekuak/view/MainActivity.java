package com.example.lekuak.view;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.lekuak.R;
import com.example.lekuak.model.LekuakDatabase;
import com.example.lekuak.model.Lugar;
import com.example.lekuak.model.Visita;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements
        LugarDialogFragment.LugarDialogListener,
        VisitaDialogFragment.VisitaDialogListener,
        LugarListFragment.OnFragmentInteractionListener,
        VisitaListFragment.OnVisitaInteractionListener,
        ConfirmarEliminarDialogFragment.ConfirmacionListener {

    private DrawerLayout drawerLayout;
    private LekuakDatabase db;
    private ExecutorService executor;
    private static final String CHANNEL_ID = "lekuak_channel";
    private Object itemSeleccionado;
    private static final int ID_ELIMINAR_LUGAR = 1;
    private static final int ID_ELIMINAR_VISITA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDark = prefs.getBoolean("modo_oscuro_activado", false);
        AppCompatDelegate.setDefaultNightMode(isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = LekuakDatabase.getInstance(this);
        executor = Executors.newSingleThreadExecutor();
        
        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        final NavController navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_todos, R.id.nav_pendientes, R.id.nav_visitas, R.id.nav_stats, R.id.nav_settings)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        final FloatingActionButton fab = findViewById(R.id.fabAddLugar);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                limpiarDetalle();

                View detailContainer = findViewById(R.id.detail_container);
                if (detailContainer != null) {
                    if (destination.getId() == R.id.nav_stats || destination.getId() == R.id.nav_settings) {
                        detailContainer.setVisibility(View.GONE);
                    } else {
                        detailContainer.setVisibility(View.VISIBLE);
                    }
                }

                if (destination.getId() == R.id.nav_visitas) {
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            VisitaDialogFragment dialog = VisitaDialogFragment.newInstance(-1, null);
                            dialog.show(getSupportFragmentManager(), "VisitaDialog");
                        }
                    });
                } else {
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LugarDialogFragment dialog = LugarDialogFragment.newInstance(null);
                            dialog.show(getSupportFragmentManager(), "LugarDialog");
                        }
                    });
                }
            }
        });
        
        if (savedInstanceState != null) {
            itemSeleccionado = savedInstanceState.getSerializable("item_seleccionado");
            if (itemSeleccionado != null) {
                if (itemSeleccionado instanceof Lugar) {
                    onLugarSelected((Lugar) itemSeleccionado);
                } else if (itemSeleccionado instanceof Visita) {
                    onVisitaSelected((Visita) itemSeleccionado);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (itemSeleccionado != null) {
            outState.putSerializable("item_seleccionado", (Serializable) itemSeleccionado);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.notif_titulo, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Avisos de Lekuak", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private void lanzarNotificacion(String nombreLugar) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                    .setContentTitle(getString(R.string.notif_titulo))
                    .setContentText(getString(R.string.notif_cuerpo, nombreLugar))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void limpiarDetalle() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.detail_container);
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                itemSeleccionado = null;
            }
        });
    }

    private void refrescarListaActual() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                if (navHostFragment != null) {
                    Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                    if (currentFragment != null) {
                        if (currentFragment instanceof LugarListFragment) {
                            ((LugarListFragment) currentFragment).recargar();
                        } else if (currentFragment instanceof VisitaListFragment) {
                            ((VisitaListFragment) currentFragment).recargar();
                        } else if (currentFragment instanceof StatsFragment) {
                            ((StatsFragment) currentFragment).recargar();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onLugarGuardado(final Lugar lugar) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (lugar.id == 0) {
                    db.lugarDao().insert(lugar);
                    lanzarNotificacion(lugar.nombre);
                } else {
                    db.lugarDao().update(lugar);
                }
                refrescarListaActual();
            }
        });
    }

    @Override
    public void onVisitaGuardada(final Visita visita) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (visita.id == 0) {
                    db.visitaDao().insert(visita);
                } else {
                    db.visitaDao().update(visita);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (itemSeleccionado instanceof Lugar && ((Lugar) itemSeleccionado).id == visita.idLugar) {
                            limpiarDetalle();
                        }
                        refrescarListaActual();
                    }
                });
            }
        });
    }

    @Override
    public void onLugarSelected(Lugar lugar) {
        itemSeleccionado = lugar;
        View detailContainer = findViewById(R.id.detail_container);
        if (detailContainer != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, LugarDetailFragment.newInstance(lugar))
                    .commit();
        } else {
            LugarDialogFragment.newInstance(lugar).show(getSupportFragmentManager(), "LugarDialog");
        }
    }

    @Override
    public void onVisitaSelected(Visita visita) {
        itemSeleccionado = visita;
        View detailContainer = findViewById(R.id.detail_container);
        if (detailContainer != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, VisitaDetailFragment.newInstance(visita))
                    .commit();
        } else {
            VisitaDialogFragment dialog = VisitaDialogFragment.newInstance(visita.idLugar, visita);
            dialog.show(getSupportFragmentManager(), "VisitaDialog");
        }
    }

    @Override
    public void onLugarLongClick(final Lugar lugar) {
        this.itemSeleccionado = lugar;
        ConfirmarEliminarDialogFragment dialog = ConfirmarEliminarDialogFragment.newInstance(
                getString(R.string.eliminar_lugar),
                getString(R.string.mensaje_eliminar, lugar.nombre),
                ID_ELIMINAR_LUGAR);
        dialog.show(getSupportFragmentManager(), "ConfirmarEliminarLugar");
    }

    @Override
    public void onVisitaLongClick(final Visita visita) {
        this.itemSeleccionado = visita;
        ConfirmarEliminarDialogFragment dialog = ConfirmarEliminarDialogFragment.newInstance(
                getString(R.string.eliminar_visita),
                getString(R.string.mensaje_eliminar_visita),
                ID_ELIMINAR_VISITA);
        dialog.show(getSupportFragmentManager(), "ConfirmarEliminarVisita");
    }

    @Override
    public void onConfirmarEliminacion(final int idItem) {
        if (idItem == ID_ELIMINAR_LUGAR && itemSeleccionado instanceof Lugar) {
            final Lugar lugar = (Lugar) itemSeleccionado;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    db.lugarDao().delete(lugar);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            limpiarDetalle();
                        }
                    });
                    refrescarListaActual();
                }
            });
        } else if (idItem == ID_ELIMINAR_VISITA && itemSeleccionado instanceof Visita) {
            final Visita visita = (Visita) itemSeleccionado;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    db.visitaDao().delete(visita);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            limpiarDetalle();
                        }
                    });
                    refrescarListaActual();
                }
            });
        }
    }

    @Override
    public void onListaActualizada(List<Lugar> lugaresNuevos) {
        if (itemSeleccionado instanceof Lugar) {
            boolean existe = false;
            for (Lugar l : lugaresNuevos) {
                if (l.id == ((Lugar) itemSeleccionado).id) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                limpiarDetalle();
            }
        }
    }
}
