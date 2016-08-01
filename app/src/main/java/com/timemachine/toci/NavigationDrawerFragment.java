package com.timemachine.toci;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerCallbacks {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * Spacing to add in RecyclerView list
     */
    private static final int VERTICAL_ITEM_SPACE = 48;
    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;
    /**
     * A pointer to deliver content to host activity (HomeMaterialActivity)
     */
//    private OnFragmentInteractionListener mListener;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private LinearLayoutManager mLayoutManager;
    private View mFragmentContainerView;
    private NavigationDrawerAdapter mAdapter;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private List<NavigationItem> navigationItems;

    // Helper fields to help store favorite settings
    Context mContext;
    AppPrefs mAppPrefs;
    List<String> mCityFavorites;

    /**
     * Begin Fragment class methods.
     * Initialize values.
     * @param savedInstanceState
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Used to save user preferences
        mContext = getActivity().getApplicationContext();
        mAppPrefs = new AppPrefs(mContext);

        // Initialize the list of navigation items and mAdapter
        navigationItems = getMenu();
        mAdapter = new NavigationDrawerAdapter(navigationItems);
        mAdapter.setNavigationDrawerCallbacks(this);
    } // End of onCreate method

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_material_drawer, container, false);
        mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(mLayoutManager);
        mDrawerList.setHasFixedSize(true);
//        // check that there are user preferences and add divider
//        if (mAppPrefs.getFavorite_cities() != null) {
//            mDrawerList.addItemDecoration(new SimpleDividerItemDecoration(getActivity(),
//                    mAppPrefs.getFavorite_cities().size()));
//        }

        mDrawerList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        selectItem(mCurrentSelectedPosition);
        return view;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        selectItem(position);
    }

    public List<NavigationItem> getMenu() {

        List<NavigationItem> items = new ArrayList<>();

        // Always show this fragment, so search is possible
        items.add(new NavigationItem("Find Crowds",
                getResources().getDrawable(R.drawable.ic_chevron_right_grey600_24dp)));

        // Added this to load favorite cities dynamically per user preferences
        if (mAppPrefs.getFavorite_cities() != null) {

            mCityFavorites = new ArrayList<>(mAppPrefs.getFavorite_cities());

            if (mCityFavorites != null) {
                for (int i = 0; i < mCityFavorites.size(); i++) {
                    items.add(new NavigationItem(mCityFavorites.get(i),
                            getResources().getDrawable(R.drawable.beer_icon)));
                }
            }
        }
        // Add crowd-specific favorites
        items.add(new NavigationItem("Favorites".toUpperCase(),
                getResources().getDrawable(R.drawable.navigation_drawer_item_empty)));
        // Add rest of fragments to app interaction
        items.add(new NavigationItem("Add Crowd".toUpperCase(),
                getResources().getDrawable(R.drawable.navigation_drawer_item_empty)));
        items.add(new NavigationItem("Sign In".toUpperCase(),
                getResources().getDrawable(R.drawable.navigation_drawer_item_empty)));
        items.add(new NavigationItem("About Us".toUpperCase(),
                getResources().getDrawable(R.drawable.navigation_drawer_item_empty)));
        return items;
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     * @param toolbar      The Toolbar of the activity.
     */
    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = (View) getActivity().findViewById(fragmentId).getParent();
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.PrimaryDarkColor));

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!isAdded()) return;
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()

                navigationItems.clear();
                navigationItems.addAll(getMenu());
                mAdapter.notifyDataSetChanged();

            }

        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
        ((NavigationDrawerAdapter) mDrawerList.getAdapter()).selectPosition(position);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

//    public void setUserData(String header, Bitmap avatar) {
//        ImageView avatarContainer = (ImageView) mFragmentContainerView.findViewById(R.id.czLogo);
//        ((TextView) mFragmentContainerView.findViewById(R.id.czMoto)).setText(header);
//        avatarContainer.setImageDrawable(new RoundImage(avatar));
//    }
//
//        public void setUserData(Bitmap avatar) {
//        ImageView avatarContainer = (ImageView) mFragmentContainerView.findViewById(R.id.czLogo);
//        avatarContainer.setImageDrawable(new RoundImage(avatar));
//    }

    public void setUserData(String header) {
        ((TextView) mFragmentContainerView.findViewById(R.id.czMoto)).setText(header);
    }

    public View getGoogleDrawer() {
        return mFragmentContainerView.findViewById(R.id.googleDrawer);
    }

    public static class RoundImage extends Drawable {
        private final Bitmap mBitmap;
        private final Paint mPaint;
        private final RectF mRectF;
        private final int mBitmapWidth;
        private final int mBitmapHeight;

        public RoundImage(Bitmap bitmap) {
            mBitmap = bitmap;
            mRectF = new RectF();
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(shader);

            mBitmapWidth = mBitmap.getWidth();
            mBitmapHeight = mBitmap.getHeight();
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawOval(mRectF, mPaint);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mRectF.set(bounds);
        }

        @Override
        public void setAlpha(int alpha) {
            if (mPaint.getAlpha() != alpha) {
                mPaint.setAlpha(alpha);
                invalidateSelf();
            }
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mPaint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public int getIntrinsicWidth() {
            return mBitmapWidth;
        }

        @Override
        public int getIntrinsicHeight() {
            return mBitmapHeight;
        }

        public void setAntiAlias(boolean aa) {
            mPaint.setAntiAlias(aa);
            invalidateSelf();
        }

        @Override
        public void setFilterBitmap(boolean filter) {
            mPaint.setFilterBitmap(filter);
            invalidateSelf();
        }

        @Override
        public void setDither(boolean dither) {
            mPaint.setDither(dither);
            invalidateSelf();
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

    }

    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;
        private int mMarker;

        public SimpleDividerItemDecoration(Context context, int marker) {
            mDivider = context.getResources().getDrawable(R.drawable.navigation_drawer_line_divider);
            mMarker = marker;
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

//            int childCount = parent.getChildCount();
//            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(mMarker);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
//            }
        }
    }
}
