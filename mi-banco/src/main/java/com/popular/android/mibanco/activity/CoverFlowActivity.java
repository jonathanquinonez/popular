package com.popular.android.mibanco.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.BuildConfig;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.TransactionListAdapter;
import com.popular.android.mibanco.animation.Rotate3dAnimation;
import com.popular.android.mibanco.base.BaseSessionActivity;
import com.popular.android.mibanco.listener.SimpleListener;
import com.popular.android.mibanco.model.AccountTransaction;
import com.popular.android.mibanco.model.Customer;
import com.popular.android.mibanco.model.CustomerAccount;
import com.popular.android.mibanco.util.AccountStatementsLoader;
import com.popular.android.mibanco.util.BitmapUtils;
import com.popular.android.mibanco.util.CameraHelper;
import com.popular.android.mibanco.util.PermissionsManagerUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.view.DialogHolo;
import com.popular.android.mibanco.view.coverflow.CoverFlow;
import com.popular.android.mibanco.view.coverflow.CoverflowImageAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides implementation of cover flow user interface.
 */
public class CoverFlowActivity extends BaseSessionActivity {

    /** The time in milliseconds for the first part of the rotation animation. */
    private final static int ANIMATION_MILIS_FIRST_PART = 500;

    /** The time in milliseconds for the second part of the rotation animation. */
    private final static int ANIMATION_MILIS_SECOND_PART = 500;

    private final static int CARD_BACK_BASE_ID = 2000;

    /** CONFIRM_PHOTO intent result code. */
    private final static int CONFIRM_PHOTO = 80;

    /** SELECT_PHOTO intent result code. */
    private final static int SELECT_PHOTO = 70;

    /** The time in milliseconds for cover flow setup animation. */
    private final static int SETUP_ANIMATION_MILIS = 1000;

    /** MAKE_PHOTO intent result code. */
    private final static int REQUEST_TAKE_PHOTO = 1;

    private final static int THOUSAND = 1000;

    private final static int FRONT_CARD_VIEW_CHILD_INDEX = 0;

    private final static int BACK_CARD_VIEW_CHILD_INDEX = 1;

    private TextView accountBalanceText;

    private TextView accountCodeText;

    private TextView accountNameText;

    private ImageView settingsButton;

    /** The cover flow view. */
    private CoverFlow coverFlow;

    /** The customer accounts list to take data from. */
    private ArrayList<CustomerAccount> customerAccounts;

    /** Account statement helper object. */
    private AccountStatementsLoader helper;

    /** The index of last selected card. */
    private int lastCardIndex;

    /** The time stamp of the last taken photo. */
    private long lastImageTimestamp;

    /** Specifies if the visible FlipperView is being animated at the moment. */
    private boolean isAnimationInProgress;

    /** Specifies the area of a card view as a rectangle. */
    private Rect cardViewAreaRectangle;

    private int state = -1;

    /** Specifies whether the currently displayed card shows the back view. */
    private boolean isBackCard;

    private boolean canFinishWhenInPortrait = true;

    private static final String CAN_FINISH_WHEN_IN_PORTRAIT = "canFinishWhenInPortrait";

    private Uri currentPhotoPath;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coverflow);

        getSupportActionBar().hide();

        cardViewAreaRectangle = new Rect();
        coverFlow = (CoverFlow) findViewById(R.id.coverflow);
        accountNameText = (TextView) findViewById(R.id.name);
        accountBalanceText = (TextView) findViewById(R.id.balance);
        accountCodeText = (TextView) findViewById(R.id.code);

        if (savedInstanceState != null) {
            lastCardIndex = savedInstanceState.getInt("lastCardIndex");
            lastImageTimestamp = savedInstanceState.getLong("lastImageTimestamp");
            canFinishWhenInPortrait = savedInstanceState.getBoolean(CAN_FINISH_WHEN_IN_PORTRAIT);
        }

        setupCoverFlow();
        setupSettingsButton();
        setupListeners();
        coverFlow.setSelection(lastCardIndex);
    }
    
    private void setupSettingsButton() {
        settingsButton = (ImageView) findViewById(R.id.photos);
        settingsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (!coverFlow.getScrollingEnabled()) {
                    return;
                }

                final DialogHolo dialog = new DialogHolo(CoverFlowActivity.this, true);
                final View customView = dialog.setCustomContentView(R.layout.coverflow_dialog_personalize);
                dialog.setTitle(R.string.coverflow_personalize_account);
                dialog.setCancelable(true);

                final Button buttonCamera = (Button) customView.findViewById(R.id.coverflow_button_take_photo);
                PackageManager packageManager = getPackageManager();
                if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !packageManager.hasSystemFeature("android.hardware.camera.front")) {
                    buttonCamera.setVisibility(View.GONE);
                }
                final Button buttonMyPhotos = (Button) customView.findViewById(R.id.coverflow_button_my_photos);
                final Button buttonUseDefault = (Button) customView.findViewById(R.id.coverflow_use_default);

                if (Utils.getAccountImagePath(getCardAccount(coverFlow.getSelectedItemPosition()), getApplicationContext()) != null) {
                    buttonUseDefault.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(final View v) {
                            final CoverflowImageAdapter adapter = (CoverflowImageAdapter) coverFlow.getAdapter();
                            Utils.clearAccountImagePath(getCardAccount(coverFlow.getSelectedItemPosition()), getApplicationContext());

                            if (adapter != null) {
                                adapter.refreshCacheAt(coverFlow.getSelectedItemPosition());
                            }

                            ((App) getApplication()).setRefreshPaymentsCardImages(true);
                            ((App) getApplication()).setRefreshTransfersCardImages(true);

                            Utils.dismissDialog(dialog);
                        }
                    });
                    buttonUseDefault.setVisibility(View.VISIBLE);
                    customView.findViewById(R.id.coverflow_use_default_separator).setVisibility(View.VISIBLE);
                }

                // take photo button - starts the camera app
                buttonCamera.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View arg0) {
                        Utils.dismissDialog(dialog);
                        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(App.getApplicationInstance().getApplicationContext(), Manifest.permission.CAMERA);
                        if(writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(CoverFlowActivity.this, new String[]{Manifest.permission.CAMERA}, MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS);
                        } else {
                            photoIntent();
                        }
                    }
                });

                // selecting photo from phone gallery
                buttonMyPhotos.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View arg0) {
                        Utils.dismissDialog(dialog);
                        final Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        Utils.setPrefsString(MiBancoConstants.CARD_ID_STRING_PREFS_KEY, Utils.getAccountImagePathKeyHash(getCardAccount(coverFlow.getSelectedItemPosition())), getApplicationContext());
                        canFinishWhenInPortrait = false;
                        startActivityForResult(intent, SELECT_PHOTO);
                        coverFlow.setScrollingEnabled(false);
                    }
                });

                Utils.showDialog(dialog, CoverFlowActivity.this);
            }

        });
    }

    private CustomerAccount getCardAccount(int cardPosition) {
        final CoverflowImageAdapter adapter = (CoverflowImageAdapter) coverFlow.getAdapter();
        if (adapter == null || adapter.getData() == null || cardPosition < 0 || cardPosition > adapter.getData().size()) {
            return null;
        }
        return adapter.getData().get(cardPosition);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO || requestCode == SELECT_PHOTO) {
                final Intent alignPhotoIntent = new Intent(CoverFlowActivity.this, AlignPhoto.class);
                if (requestCode == SELECT_PHOTO) {
                    final Uri selectedImageUri = data.getData();
                    final String path = getRealPathFromURI(selectedImageUri);
                    if (path == null) {
                        return;
                    }
                    alignPhotoIntent.putExtra("path", path);
                } else {
                    alignPhotoIntent.putExtra("path", CameraHelper.getCameraUriPathFromPrefs(getApplicationContext()));
                    alignPhotoIntent.putExtra("orientation", getProperExif(CameraHelper.getCameraUriPathFromPrefs(getApplicationContext())));
                }
                alignPhotoIntent.putExtra("width", coverFlow.getImageWidth());
                alignPhotoIntent.putExtra("height", coverFlow.getImageHeight());
                alignPhotoIntent.putExtra("cardId", Utils.getPrefsString(MiBancoConstants.CARD_ID_STRING_PREFS_KEY, getApplicationContext()));
                startActivityForResult(alignPhotoIntent, CONFIRM_PHOTO);

            }
        } else if (resultCode == RESULT_CANCELED) {
            coverFlow.setScrollingEnabled(true);
            if (requestCode == REQUEST_TAKE_PHOTO || requestCode == SELECT_PHOTO) {
                if (requestCode == REQUEST_TAKE_PHOTO)
                    deleteTempImage();

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    finish();
            }
        }

        if (requestCode == REQUEST_TAKE_PHOTO || requestCode == SELECT_PHOTO) {
            canFinishWhenInPortrait = true;
        }

        if (requestCode == CONFIRM_PHOTO) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MiBancoConstants.REQUEST_CODE_ASK_PERMISSIONS) {
            boolean permissionGranted = PermissionsManagerUtils.isFunctionalityAllowed(this, permissions, grantResults,
                    R.string.permission_custom_image_mandatory, Utils.openPermissionSettings(this));
            if (permissionGranted)
                photoIntent();
        }
    }

    public void photoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Uri cameraUri;
            String filePath;
            File photoFile = CameraHelper.createTempImageFile(this);
            //Uri is going to be generated through two different methods depending of android version used
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cameraUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                filePath = photoFile.getPath();
            } else {
                cameraUri = Uri.fromFile(photoFile);
                filePath = cameraUri.getPath();
            }
            currentPhotoPath = cameraUri;
            CameraHelper.setCameraUriPathFromPrefs(filePath, this);
            Utils.setPrefsString(MiBancoConstants.CARD_ID_STRING_PREFS_KEY, Utils.getAccountImagePathKeyHash(getCardAccount(coverFlow.getSelectedItemPosition())),
                    this);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoPath);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        } else {
            Toast.makeText(this, getString(R.string.no_application_found), Toast.LENGTH_LONG).show();
        }

        coverFlow.setScrollingEnabled(false);
    }

    private void deleteTempImage() {
        CameraHelper.deleteTempImage(this, currentPhotoPath);
        currentPhotoPath = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lastCardIndex", lastCardIndex);
        outState.putLong("lastImageTimestamp", lastImageTimestamp);
        outState.putBoolean(CAN_FINISH_WHEN_IN_PORTRAIT, canFinishWhenInPortrait);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);

        return true;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, R.string.coverflow_rotate_info, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            try {
                ((App) getApplication()).stopLastLaunchedTask();
            } catch (final Exception e) {
                Log.w("CoverFLowActivity", e);
            }

            if (canFinishWhenInPortrait) {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Display disp = getWindowManager().getDefaultDisplay();

        // portrait
        if (disp.getHeight() > disp.getWidth()) {
            onBackPressed();
            return;
        }

        CoverflowImageAdapter adapter = (CoverflowImageAdapter) coverFlow.getAdapter();
        if (adapter != null) {
            adapter.refreshCacheAt(coverFlow.getSelectedItemPosition());
        }
        coverFlow.setScrollingEnabled(true);
    }

    @Override
    protected void onDestroy() {
        if (coverFlow != null) {
            if (coverFlow.getAdapter() != null) {
                ((CoverflowImageAdapter) coverFlow.getAdapter()).recycle();
            }
        }
        super.onDestroy();
    }

    @Override
    public boolean onTrackballEvent(final MotionEvent event) {
        if (coverFlow.getScrollingEnabled()) {
            return super.onTrackballEvent(event);
        }
        return true;
    }

    /**
     * Displays 1st part of the 3D rotation animation of a card view.
     * 
     * @param vf the corresponding ViewFlipper object
     * @param position the position
     */
    private void applyRotation(final ViewFlipper vf, final int position) {
        final float centerX = vf.getWidth() / 2.0f;
        final float centerY = vf.getHeight() / 2.0f;
        final Rotate3dAnimation rotation = new Rotate3dAnimation(0, 90, centerX, centerY, 0, false);
        rotation.setDuration(ANIMATION_MILIS_FIRST_PART);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(final Animation arg0) {
                vf.showNext();
                isBackCard = (vf.getDisplayedChild() != FRONT_CARD_VIEW_CHILD_INDEX);
                secondPart(vf, position);
            }

            @Override
            public void onAnimationRepeat(final Animation arg0) {
            }

            @Override
            public void onAnimationStart(final Animation arg0) {
            }

        });
        vf.startAnimation(rotation);
    }

    // http://stackoverflow.com/questions/8450539/images-taken-with-action-image-capture-always-returns-1-for-exifinterface-tag-or/8864367#8864367
    private int getProperExif(final String filePath) {
        int rotation = -1;
        final long fileSize = new File(filePath).length();

        final Cursor mediaCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.MediaColumns.SIZE },
                MediaStore.MediaColumns.DATE_ADDED + ">=?", new String[] { String.valueOf(lastImageTimestamp / THOUSAND - 1) }, MediaStore.MediaColumns.DATE_ADDED + " desc");

        if (mediaCursor != null && lastImageTimestamp != 0 && mediaCursor.getCount() != 0) {
            while (mediaCursor.moveToNext()) {
                final long size = mediaCursor.getLong(1);
                // Extra check to make sure that we are getting the orientation
                // from the proper file
                if (size == fileSize) {
                    rotation = mediaCursor.getInt(0);
                    break;
                }
            }
            mediaCursor.close();
        }

        return rotation;
    }

    /**
     * Gets the real path from URI.
     * 
     * @param contentUri the content URI
     * @return the real path from URI
     */
    public String getRealPathFromURI(final Uri contentUri) {
        try {
            final String[] proj = { MediaColumns.DATA };
            final Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            final int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } catch (final Exception e) {
            Log.w("CoverFlowActivity", e);
        }

        return null;
    }

    /**
     * Displays 2nd part of the 3D rotation animation of a card view.
     * 
     * @param vf the corresponding ViewFlipper object
     * @param position the position
     */
    private void secondPart(final ViewFlipper vf, final int position) {
        final float centerX = vf.getWidth() / 2.0f;
        final float centerY = vf.getHeight() / 2.0f;
        final Rotate3dAnimation rotation = new Rotate3dAnimation(-90, 0, centerX, centerY, 0, false);
        rotation.setDuration(ANIMATION_MILIS_SECOND_PART);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(final Animation arg0) {
                state = vf.getCurrentView().getId() < CARD_BACK_BASE_ID ? -1 : position;
                cardViewAreaRectangle = new Rect(vf.getLeft(), vf.getTop(), vf.getRight(), vf.getBottom());

                if (isBackCard) {
                    final CustomerAccount account = customerAccounts.get(position);
                    final String comment = account.getAccountLast4Num();
                    final String name = account.getNickname();
                    final String value = account.getPortalBalance();
                    final String accountNumber = account.getFrontEndId();
                    final View backCardView = vf.getChildAt(BACK_CARD_VIEW_CHILD_INDEX);

                    final ImageView accountImageView = (ImageView) backCardView.findViewById(R.id.item_image);

                    final String path = Utils.getAccountImagePath(account, getApplicationContext());
                    final Integer image = account.getGalleryImgResource();
                    if (path != null) {
                        accountImageView.setBackgroundDrawable(new BitmapDrawable(getResources(), ((CoverflowImageAdapter) coverFlow.getAdapter()).getItemBitmap(position)));
                    } else {
                        Bitmap tmp = BitmapUtils.decodeSampledBitmapFromFile(path, (int) ((CoverflowImageAdapter) coverFlow.getAdapter()).getWidth(),
                                (int) ((CoverflowImageAdapter) coverFlow.getAdapter()).getHeight());
                        if (tmp != null) {
                            accountImageView.setImageBitmap(tmp);
                        } else {
                            accountImageView.setBackgroundResource(image);
                        }
                        tmp = null;
                    }

                    final TextView tvName = (TextView) backCardView.findViewById(R.id.item_name);
                    tvName.setText(name);
                    final TextView tvValue = (TextView) backCardView.findViewById(R.id.item_value);
                    tvValue.setText(value);
                    if (account.isBalanceColorRed()) {
                        tvValue.setTextColor(Color.RED);
                    }
                    final TextView tvComment = (TextView) backCardView.findViewById(R.id.item_comment);
                    tvComment.setText(comment);

                    // fill list
                    final ListView statementList = (ListView) backCardView.findViewById(R.id.transaction_list);
                    final SimpleListener listener = new SimpleListener() {

                        @Override
                        public void done() {
                            final List<AccountTransaction> filteredTransactions = AccountStatementsLoader.filter("", helper.getAllSortedTransactions());
                            final TransactionListAdapter listAdapter = new TransactionListAdapter(CoverFlowActivity.this, filteredTransactions, AccountStatementsLoader.getStatementRangeString(
                                    CoverFlowActivity.this, helper.getCurrentTransactions().getCycle(1), filteredTransactions), account.getSubtype(), account.getFrontEndId());
                            statementList.setAdapter(listAdapter);
                            listAdapter.notifyDataSetChanged();
                        }
                    };
                    helper = new AccountStatementsLoader(CoverFlowActivity.this, (App) getApplication(), (Button) backCardView.findViewById(R.id.button_more_transactions), accountNumber, 1, listener);
                    helper.refreshList();
                }
                isAnimationInProgress = false;
            }

            @Override
            public void onAnimationRepeat(final Animation arg0) {
            }

            @Override
            public void onAnimationStart(final Animation arg0) {
            }

        });

        vf.startAnimation(rotation);
    }

    /**
     * Setup cover flow.
     */
    private void setupCoverFlow() {
        if (coverFlow.getAdapter() != null) {
            ((CoverflowImageAdapter) coverFlow.getAdapter()).recycle();
        }

        customerAccounts = new ArrayList<CustomerAccount>();

        Customer loggedInCustomer = application.getLoggedInUser();
        if (loggedInCustomer != null) {
            customerAccounts.addAll(loggedInCustomer.getCreditCards());
            customerAccounts.addAll(loggedInCustomer.getDepositAccounts());
            customerAccounts.addAll(loggedInCustomer.getLoans());
            customerAccounts.addAll(loggedInCustomer.getMortgage());
            customerAccounts.addAll(loggedInCustomer.getCdsIras());
            customerAccounts.addAll(loggedInCustomer.getOtherAccounts());
            customerAccounts.addAll(loggedInCustomer.getRewards());
            customerAccounts.addAll(loggedInCustomer.getInsuranceAndSecurities());
        } else {
            application.reLogin(CoverFlowActivity.this);
            return;
        }

        CoverflowImageAdapter coverflowImageAdapter = new CoverflowImageAdapter(this, customerAccounts, coverFlow.getImageWidth(), coverFlow.getImageHeight(), coverFlow.getSelectedItemPosition());
        coverflowImageAdapter.setScaledWidth(coverFlow.getImageWidth());
        coverflowImageAdapter.setScaledHeight(coverFlow.getImageHeight());
        coverFlow.setAdapter(coverflowImageAdapter);
        coverFlow.setAnimationDuration(SETUP_ANIMATION_MILIS);
    }

    /**
     * Sets the listeners.
     * 
     */
    private void setupListeners() {
        coverFlow.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                if (!isAnimationInProgress && customerAccounts.get(position).showStatement() && (state < 0 || state == position)) {
                    if (coverFlow.getSelectedItemPosition() == position) {
                        if (!isAnimationInProgress) {
                            performOnItemClick();
                        }
                    }
                }
            }

        });

        coverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final CustomerAccount selectedAccount = ((CoverflowImageAdapter) ((CoverFlow) parent).getAdapter()).getCustomerAccount(position);
                accountNameText.setText(selectedAccount.getNickname());
                accountBalanceText.setText(selectedAccount.getPortalBalance());
                accountCodeText.setText(selectedAccount.getAccountLast4Num());
                lastCardIndex = position;
                ((CoverflowImageAdapter) coverFlow.getAdapter()).setCurrentPosition(position);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });

        coverFlow.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                if (isAnimationInProgress) {
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    return true;
                }

                if (!coverFlow.getScrollingEnabled()) {
                    if (isBackCard) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                            performOnItemClick();
                            return true;
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                        return !cardViewAreaRectangle.contains((int) event.getX(), (int) event.getY());
                    }
                    return true;
                }

                return false;
            }
        });
    }

    /** Actions to perform on item click. */
    private void performOnItemClick() {
        coverFlow.invalidate();
        isAnimationInProgress = true;
        coverFlow.setScrollingEnabled(!coverFlow.getScrollingEnabled());
        if (settingsButton.getVisibility() == View.VISIBLE) {
            settingsButton.setVisibility(View.INVISIBLE);
        } else {
            settingsButton.setVisibility(View.VISIBLE);
        }
        applyRotation((ViewFlipper) coverFlow.getSelectedView(), coverFlow.getSelectedItemPosition());
    }
}
