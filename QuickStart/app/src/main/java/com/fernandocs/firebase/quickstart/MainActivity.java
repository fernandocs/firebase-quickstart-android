package com.fernandocs.firebase.quickstart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fernandocs.firebase.quickstart.fragment.MyPostsFragment;
import com.fernandocs.firebase.quickstart.fragment.MyTopPostsFragment;
import com.fernandocs.firebase.quickstart.fragment.RecentPostsFragment;
import com.fernandocs.firebase.quickstart.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 100;
    private static final int RC_SIGN_OUT = 101;
    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mAuth.getCurrentUser() == null) {
            startSignIn();
        } else {
            initUI();
        }
    }

    private void startSignIn() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(AuthUI.getDefaultTheme())
                        .setLogo(R.drawable.logo)
                        .setProviders(AuthUI.EMAIL_PROVIDER, AuthUI.FACEBOOK_PROVIDER, AuthUI.GOOGLE_PROVIDER)
                        .setTosUrl("https://www.firebase.com/terms/terms-of-service.html")
                        .build(),
                RC_SIGN_IN);
    }

    private void initUI() {
        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{
                    new RecentPostsFragment(),
                    new MyPostsFragment(),
                    new MyTopPostsFragment(),
            };
            private final String[] mFragmentNames = new String[]{
                    getString(R.string.heading_recent),
                    getString(R.string.heading_my_posts),
                    getString(R.string.heading_my_top_posts)
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Button launches NewPostActivity
        findViewById(R.id.fab_new_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        if (requestCode == RC_SIGN_OUT) {
            if (resultCode == RESULT_OK) {
                startSignIn();
            }
            return;
        }

        showSnackbar(R.string.unknown_response);
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            onAuthSuccess();
            initUI();
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            finish();
            return;
        }

        showSnackbar(R.string.unknown_sign_in_response);
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void onAuthSuccess() {
        FirebaseUser user = mAuth.getCurrentUser();
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail(), user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
    }

    private void writeNewUser(String userId, String name, String email, String photoUrl) {
        User user = new User(name, email, photoUrl);

        mDatabase.child("users").child(userId).setValue(user);
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(findViewById(android.R.id.content), errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_profile:
                startActivityForResult(new Intent(this, ProfileActivity.class), RC_SIGN_OUT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
