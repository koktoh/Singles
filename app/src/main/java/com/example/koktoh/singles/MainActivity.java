package com.example.koktoh.singles;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.skyway.Peer.Browser.Canvas;
import io.skyway.Peer.Browser.MediaConstraints;
import io.skyway.Peer.Browser.MediaStream;
import io.skyway.Peer.Browser.Navigator;
import io.skyway.Peer.CallOption;
import io.skyway.Peer.DataConnection;
import io.skyway.Peer.MediaConnection;
import io.skyway.Peer.OnCallback;
import io.skyway.Peer.Peer;
import io.skyway.Peer.PeerError;
import io.skyway.Peer.PeerOption;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE = 1;

    private Peer mPeer;
    private MediaConnection mMConnect;
    private DataConnection mDConnect;

    private MediaStream mSLocal;
    private MediaStream mSRemote;

    private Handler mHandler;

    private String mId;
    private String[] mPeerIdList;

    private boolean mCalling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<String> permissionList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.CAMERA);
            }

            if (this.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.RECORD_AUDIO);
            }

            if (permissionList.size() > 0) {
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE);
            }
        }

        mHandler = new Handler(Looper.getMainLooper());

        peerInit();

        getLocalStream();

        mCalling = false;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listingPeers();
            }
        });

        ImageButton forward = (ImageButton) findViewById(R.id.forward);
        forward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                }
                return false;
            }
        });

        ImageButton backward = (ImageButton) findViewById(R.id.forward);
        backward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                }
                return false;
            }
        });

        ImageButton right = (ImageButton) findViewById(R.id.forward);
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                }
                return false;
            }
        });

        ImageButton left = (ImageButton) findViewById(R.id.forward);
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                }
                return false;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since camera access has not been granted.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void peerInit() {
        PeerOption option = new PeerOption();

        option.key = API.Key;
        option.domain = API.Domain;

        mPeer = new Peer(getApplicationContext(), option);
        setPeerCallback(mPeer);
    }

    private void getLocalStream() {
        Navigator.initialize(mPeer);
        MediaConstraints constraints = new MediaConstraints();
        mSLocal = Navigator.getUserMedia(constraints);

     //   Canvas canvas = (Canvas) findViewById(R.id.canvas);
     //   canvas.addSrc(mSLocal, 0);
    }

    private void call(String id) {
        if (mPeer == null) {
            return;
        }

        if (mMConnect != null) {
            mMConnect.close();
            mMConnect = null;
        }

        CallOption option = new CallOption();

        mMConnect = mPeer.call(id, mSLocal, option);

        if (mMConnect != null) {
            setMediaCallback(mMConnect);
            mCalling = true;
        }

        updateUI();
    }

    private void setPeerCallback(Peer peer) {
        peer.on(Peer.PeerEventEnum.OPEN, new OnCallback() {
            @Override
            public void onCallback(Object o) {
                if (o instanceof String) {
                    mId = (String) o;

                    Log.d(TAG, mId);

                    updateUI();
                }
            }
        });

        peer.on(Peer.PeerEventEnum.CALL, new OnCallback() {
            @Override
            public void onCallback(Object o) {
                if (!(o instanceof MediaConnection)) {
                    return;
                }

                mMConnect = (MediaConnection) o;

                mMConnect.answer(mSLocal);

                setMediaCallback(mMConnect);

                mCalling = true;

                updateUI();
            }
        });

        peer.on(Peer.PeerEventEnum.CLOSE, new OnCallback() {
            @Override
            public void onCallback(Object o) {

            }
        });

        peer.on(Peer.PeerEventEnum.DISCONNECTED, new OnCallback() {
            @Override
            public void onCallback(Object o) {

            }
        });

        peer.on(Peer.PeerEventEnum.ERROR, new OnCallback() {
            @Override
            public void onCallback(Object o) {
                PeerError error = (PeerError) o;
                Log.e(TAG, error.type.toString());
            }
        });

        updateUI();
    }

    private void unsetPeerCallback(Peer peer) {
        peer.on(Peer.PeerEventEnum.OPEN, null);
        peer.on(Peer.PeerEventEnum.CONNECTION, null);
        peer.on(Peer.PeerEventEnum.CALL, null);
        peer.on(Peer.PeerEventEnum.CLOSE, null);
        peer.on(Peer.PeerEventEnum.DISCONNECTED, null);
        peer.on(Peer.PeerEventEnum.ERROR, null);
    }

    private void setMediaCallback(MediaConnection media) {
        media.on(MediaConnection.MediaEventEnum.STREAM, new OnCallback() {
            @Override
            public void onCallback(Object o) {
                mSRemote = (MediaStream) o;

                Canvas canvas = (Canvas) findViewById(R.id.canvas);
                canvas.addSrc(mSRemote, 0);
            }
        });

        media.on(MediaConnection.MediaEventEnum.CLOSE, new OnCallback() {
            @Override
            public void onCallback(Object o) {
                if (mSRemote == null) {
                    return;
                }

                Canvas canvas = (Canvas) findViewById(R.id.canvas);
                canvas.addSrc(mSRemote, 0);

                mSRemote = null;

                mMConnect = null;
                mCalling = false;

                updateUI();
            }
        });

        media.on(MediaConnection.MediaEventEnum.ERROR, new OnCallback() {
            @Override
            public void onCallback(Object o) {
                PeerError error = (PeerError) o;

                Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void unsetMediaCallback(MediaConnection media) {
        media.on(MediaConnection.MediaEventEnum.STREAM, null);
        media.on(MediaConnection.MediaEventEnum.CLOSE, null);
        media.on(MediaConnection.MediaEventEnum.ERROR, null);
    }

    private void listingPeers() {
        if (mPeer == null || mId == null || mId.length() == 0) {
            return;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        mPeer.listAllPeers(new OnCallback() {
            @Override
            public void onCallback(Object o) {
                if (!(o instanceof JSONArray)) {
                    return;
                }

                JSONArray peers = (JSONArray) o;

                StringBuilder builder = new StringBuilder();

                for (int i = 0; i < peers.length(); i++) {
                    String str = "";
                    try {
                        str = peers.getString(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (mId.compareToIgnoreCase(str) == 0) {
                        continue;
                    }

                    if (builder.length() > 0) {
                        builder.append(",");
                    }

                    builder.append(str);
                }

                String str = builder.toString();
                mPeerIdList = str.split(",");

                if (mPeerIdList != null && mPeerIdList.length > 0) {
                    call(mPeerIdList[0]);
                    Log.d(TAG, mPeerIdList[0]);
                }
            }
        });
    }

    private void selectPeer() {
        if (mHandler == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private void closing() {
        if (!mCalling) {
            return;
        }

        mCalling = false;

        if (mMConnect != null) {
            mMConnect.close();
        }
    }

    private void updateUI() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                MenuItem item = navigationView.getMenu().findItem(R.id.nav_id);
                Log.d(TAG, "" + (item == null));
                if (mId != null) {
                    item.setTitle(mId);
                }
            }
        });
    }

    private void destroyPeer() {
        closing();

        if (mSRemote != null) {
            Canvas canvas = (Canvas) findViewById(R.id.canvas);
            canvas.removeSrc(mSRemote, 0);

            mSRemote.close();
            mSRemote = null;
        }

        if (mSLocal != null) {
            Canvas canvas = (Canvas) findViewById(R.id.canvas);
            canvas.removeSrc(mSLocal, 0);

            mSLocal.close();
            mSLocal = null;
        }

        if (mMConnect != null) {
            if (mMConnect.isOpen) {
                mMConnect.close();
            }

            unsetMediaCallback(mMConnect);
            mMConnect = null;
        }

        Navigator.terminate();

        if (mPeer != null) {
            unsetPeerCallback(mPeer);

            if (!mPeer.isDisconnected) {
                mPeer.disconnect();
            }

            if (!mPeer.isDestroyed) {
                mPeer.destroy();
            }

            mPeer = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    @Override
    protected void onPause() {
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        destroyPeer();

        mPeerIdList = null;
        mHandler = null;

        super.onDestroy();
    }
}
