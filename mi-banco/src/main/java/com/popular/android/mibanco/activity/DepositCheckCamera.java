package com.popular.android.mibanco.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Deposit Check - Camera Activity class.
 */
public class DepositCheckCamera extends BaseSessionActivity implements SurfaceHolder.Callback {
	
	/** The flash variables */
	private static final int FLASH_OFF = 0;
	private static final int FLASH_ON = 1;
	private static final int FLASH_AUTO = 2;
	
	/** The state (action) possible */
	private static final int FRONT_PICTURE = 0;
	private static final int FRONT_CONFIRM = 1;
	private static final int BACK_PICTURE = 2;
	private static final int BACK_CONFIRM = 3;
	
	/** The current action */
	private static final String CURRENT_ACTION = "current_action";
	private static final String CURRENT_BITMAP = "current_bitmap";
	private static final String TAKE_FRONT_PICTURE = "take_front_picture";
	private int currentAction = FRONT_PICTURE;
	
	/** The current flash setting */
	private int currentFlashSetting = 0;
	
	/** The textview to display the flash status */
	private TextView cameraFlashStatus;
	
	/** The button to take the picture */
	private LinearLayout pictureButton;
	
	/** The button to use the picture */
	private Button useButton;
	
	/** The textview that displays the front photo instructions */
	private TextView frontPhotoInstructions;
	
	/** The textview that displays the back photo instructions */
	private TextView backPhotoInstructions;
	
	/** The guidelines images */
	private ImageView guidelines_left;
	private ImageView guidelines_right;
	
	/** The surface view for the camera */
	private SurfaceView cameraSurfaceView;
	private SurfaceHolder holder;
	
	/** The camera item */
	private Camera camera;
	
	/** The progress bar */
	private ProgressBar progressBar;
	
	/** The image view where the preview will be displayed */
	private ImageView cameraPreviewImage;
	
	/** The byte array to hold the picture taken */
	private byte[] pictureTaken;
	
	/** The bitmaps to use over and over again */
	Bitmap bmp = null;
	Bitmap croppedBitmap = null;
	
	/** Configure the camera only once */
	private boolean cameraConfigured = false;
	
	@Override
	@SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
    	
		super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit_check_camera);

		TextView photoBeingTaken = (TextView) findViewById(R.id.camera_check_side);
		TextView cancelButton = (TextView) findViewById(R.id.camera_cancel);
		ImageView flashSettings = (ImageView) findViewById(R.id.flash);

        cameraFlashStatus = (TextView) findViewById(R.id.camera_flash_status);
        pictureButton = (LinearLayout) findViewById(R.id.camera_clickable_area);
        useButton = (Button) findViewById(R.id.camera_use_button);
        frontPhotoInstructions = (TextView) findViewById(R.id.camera_text_top);
        backPhotoInstructions = (TextView) findViewById(R.id.camera_text_bottom);
        guidelines_left = (ImageView) findViewById(R.id.guidelines_left);
        guidelines_right = (ImageView) findViewById(R.id.guidelines_right);
        
        if(savedInstanceState != null) {
        	currentAction = savedInstanceState.getInt(CURRENT_ACTION);
        	if(currentAction == FRONT_PICTURE) {
        		photoBeingTaken.setText(R.string.camera_front_side);
        		frontPhotoInstructions.setVisibility(View.VISIBLE);
        		backPhotoInstructions.setVisibility(View.GONE);
        		guidelines_left.setVisibility(View.VISIBLE);
        		guidelines_right.setVisibility(View.VISIBLE);
        	}
        	else if(currentAction == FRONT_CONFIRM) {
        		photoBeingTaken.setText(R.string.camera_front_side);
        		frontPhotoInstructions.setVisibility(View.GONE);
        		backPhotoInstructions.setVisibility(View.GONE);
        		guidelines_left.setVisibility(View.GONE);
        		guidelines_right.setVisibility(View.GONE);
        	}
        	else if(currentAction == BACK_PICTURE) {
        		photoBeingTaken.setText(R.string.camera_back_side);
        		frontPhotoInstructions.setVisibility(View.VISIBLE);
        		backPhotoInstructions.setVisibility(View.VISIBLE);
        		guidelines_left.setVisibility(View.VISIBLE);
        		guidelines_right.setVisibility(View.VISIBLE);
        	}
        	else if(currentAction == BACK_CONFIRM) {
        		photoBeingTaken.setText(R.string.camera_back_side);
        		frontPhotoInstructions.setVisibility(View.GONE);
        		backPhotoInstructions.setVisibility(View.GONE);
        		guidelines_left.setVisibility(View.GONE);
        		guidelines_right.setVisibility(View.GONE);
        	}
        }
        else {
        	boolean frontPictureToBeTaken = getIntent().getBooleanExtra(TAKE_FRONT_PICTURE, true);
        	if(frontPictureToBeTaken) {
        		currentAction = FRONT_PICTURE;
        		photoBeingTaken.setText(R.string.camera_front_side);
        		frontPhotoInstructions.setVisibility(View.VISIBLE);
        		backPhotoInstructions.setVisibility(View.GONE);
        		guidelines_left.setVisibility(View.VISIBLE);
        		guidelines_right.setVisibility(View.VISIBLE);
        	}
        	else {
        		currentAction = BACK_PICTURE;
        		photoBeingTaken.setText(R.string.camera_back_side);
        		frontPhotoInstructions.setVisibility(View.VISIBLE);
        		backPhotoInstructions.setVisibility(View.VISIBLE);
        		guidelines_left.setVisibility(View.VISIBLE);
        		guidelines_right.setVisibility(View.VISIBLE);
        	}
        }
        
        if(getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
        	currentFlashSetting = application.getDepositCheckFlashStatus();
        	
        	if(currentFlashSetting == FLASH_OFF)
        		cameraFlashStatus.setText(R.string.camera_flash_off);
        	else if(currentFlashSetting == FLASH_ON)
        		cameraFlashStatus.setText(R.string.camera_flash_on);
        	else if(currentFlashSetting == FLASH_AUTO)
        		cameraFlashStatus.setText(R.string.camera_flash_auto);
        	
        	// If we click the flash image, change the setting
        	flashSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                	handleFlashClick();
                }
            });
        	
        	cameraFlashStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                	handleFlashClick();
                }
        	});
        }
        
        // Set up the camera
        cameraPreviewImage = (ImageView) findViewById(R.id.camera_imageview);
        progressBar = (ProgressBar) findViewById(R.id.camera_progress_bar);
        cameraSurfaceView = (SurfaceView) findViewById(R.id.camera_surfaceview);
        holder = cameraSurfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Handle the picture button presses
        pictureButton.setClickable(true);
        pictureButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// If the camera was garbage-collected, re instantiate here
				if(camera == null) {
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
						camera = Camera.open(0);
					else
						camera = Camera.open();
				}
				
				if(camera != null) {
					// Set up the correct flash setting
					boolean switchCameraFlash = getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
					Camera.Parameters parameters = camera.getParameters();
					
					if(switchCameraFlash) {
					
						if(currentFlashSetting == FLASH_OFF) {
							parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
						}
						else if(currentFlashSetting == FLASH_ON) {
				    		parameters.setFlashMode(Parameters.FLASH_MODE_ON);
			    		}
						else if(currentFlashSetting == FLASH_AUTO) {
			    			parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
			    		}
                        setFocus(parameters);
						camera.setParameters(parameters);
					}

					/**
					 * TODO: LALR 	java.lang.RuntimeException DepositCheckCamera.java line 241 in com.popular.android.mibanco.activity.DepositCheckCamera$3.onClick()
					 * autoFocus failed
					 */
					if(parameters.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_AUTO) ||
                            parameters.getFocusMode().equals(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
						camera.autoFocus(autoFocusCallBack);
					}
					else {
						camera.takePicture(shutterCallback, null, pictureCallback);
					}
				}

				switch(currentAction) {
	        		case FRONT_PICTURE:
	        			currentAction = FRONT_CONFIRM;
	        			break;

	        		case BACK_PICTURE:
	        			currentAction = BACK_CONFIRM;
	            		break;
					default:
						break;
				}
			}
		});
        
        // Handle the Use button presses
        useButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Set the front or back image
				if(currentAction == FRONT_PICTURE || currentAction == FRONT_CONFIRM) {
					application.setDepositCheckFrontImage(pictureTaken);
					pictureTaken = null;
				}
				else if(currentAction == BACK_PICTURE || currentAction == BACK_CONFIRM) {
					application.setDepositCheckBackImage(pictureTaken);
					pictureTaken = null;
				}
				
				// Release the camera
				if(camera != null) {
					camera.release();
					camera = null;
				}
				
				// Free up the memory
				if(bmp != null)
					bmp.recycle();
				
				if(croppedBitmap != null)
					croppedBitmap.recycle();
				
				bmp = null;
				croppedBitmap = null;
				
				Intent output = new Intent();
				boolean takeFrontPicture = (currentAction == FRONT_PICTURE || currentAction == FRONT_CONFIRM);
				output.putExtra(TAKE_FRONT_PICTURE, takeFrontPicture);
				setResult(RESULT_OK, output);
				finish();
			}
		});
        
        // Go backwards to the previous action if the X is pressed
        cancelButton.setOnClickListener(new View.OnClickListener() {
            
        	@Override
            public void onClick(final View v) {
            	switch(currentAction) {
	            	case FRONT_PICTURE:
	            	case BACK_PICTURE:
	            		Intent output = new Intent();
	                	setResult(Activity.RESULT_CANCELED, output);
	                    finish();
	                    break;

	            	case FRONT_CONFIRM:
	            		//configure Camera again, since it lose focus mode on reset
	            	    cameraConfigured = false;
	            		currentAction = FRONT_PICTURE;
	            		cameraSurfaceView.setVisibility(View.VISIBLE);
	            		cameraPreviewImage.setVisibility(View.INVISIBLE);
	            		if(camera != null)
	            			camera.startPreview();
	            		hideUseButton();
	            		break;

	            	case BACK_CONFIRM:
						//configure Camera again, since it lose focus mode on reset
                        cameraConfigured = false;
	            		currentAction = BACK_PICTURE;
	            		cameraSurfaceView.setVisibility(View.VISIBLE);
	            		cameraPreviewImage.setVisibility(View.INVISIBLE);
	            		if(camera != null)
	            			camera.startPreview();
	            		hideUseButton();
	            		break;
					default:
						break;
            	}
            }
        });
        
        if(currentAction == FRONT_CONFIRM || currentAction == BACK_CONFIRM) {
        	if(camera != null)
        		camera.stopPreview();

        	if(savedInstanceState != null) {
        		pictureTaken = savedInstanceState.getByteArray(CURRENT_BITMAP);
        		bmp = BitmapFactory.decodeByteArray(pictureTaken, 0, pictureTaken.length);
        		cameraPreviewImage.setImageBitmap(bmp);
        	}
        	
        	hideGuides();
			cameraSurfaceView.setVisibility(View.INVISIBLE);
			cameraPreviewImage.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
        	showUseButton();
        }
        else {
        	hideUseButton();
        }
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(CURRENT_ACTION, currentAction);
		
		if((currentAction == FRONT_CONFIRM || currentAction == BACK_CONFIRM) && cameraPreviewImage.getDrawable() != null) {
			Drawable drawable = cameraPreviewImage.getDrawable();
			if (drawable instanceof BitmapDrawable) {
				BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
				croppedBitmap = bitmapDrawable.getBitmap();
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				outState.putByteArray(CURRENT_BITMAP, byteArray);
			
				if(bmp != null)
					bmp.recycle();
				
				if(croppedBitmap != null)
					croppedBitmap.recycle();
				
				bmp = null;
				croppedBitmap = null;
				byteArray = null;
				System.gc();
			}
		}
        super.onSaveInstanceState(outState);
    }
    
	@TargetApi(9)
	@Override
	public void onResume() {
		super.onResume();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
			camera = Camera.open(0);
		else
			camera = Camera.open();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(camera != null) {
			camera.release();
			camera = null;
		}
	}

	/**
	 * callback: autoFocusCallBack
	 *
     * Handles auto-focus for the camera.
     *
     */
	Camera.AutoFocusCallback autoFocusCallBack = new Camera.AutoFocusCallback() {
	    @Override
	    public void onAutoFocus(boolean success, Camera camera) {
	    	camera.takePicture(shutterCallback, null, pictureCallback);
	    	camera.cancelAutoFocus();
	    	setFocus(camera.getParameters());

	    }
	};

	/**
	 * function: handleFlashClick
	 *
     * Handle a press on either the flash icon or the flash status;
     * changes the flash status to the next available one.
     *
     */
	private void handleFlashClick() {
		if(currentFlashSetting == FLASH_OFF) {
			currentFlashSetting = FLASH_ON;
			cameraFlashStatus.setText(R.string.camera_flash_on);
		}
		else if(currentFlashSetting == FLASH_ON) {
			currentFlashSetting = FLASH_AUTO;
			cameraFlashStatus.setText(R.string.camera_flash_auto);
		}
		else if(currentFlashSetting == FLASH_AUTO) {
			currentFlashSetting = FLASH_OFF;
			cameraFlashStatus.setText(R.string.camera_flash_off);
		}

		application.setDepositCheckFlashStatus(currentFlashSetting);
		
		if(camera != null) {
			// Set up the correct flash setting
			Camera.Parameters parameters = camera.getParameters();
			if(currentFlashSetting == FLASH_OFF) {
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			}
			else if(currentFlashSetting == FLASH_ON) {
		    	parameters.setFlashMode(Parameters.FLASH_MODE_ON);
	    	}
			else if(currentFlashSetting == FLASH_AUTO) {
	    		parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
	    	}
			camera.setParameters(parameters);
		}
	}

	/**
	 * function: showUseButton
	 *
     * Show the proper controls after a picture is taken.
     *
     */
	private void showUseButton() {
		findViewById(R.id.flash).setVisibility(View.GONE);
		cameraFlashStatus.setVisibility(View.GONE);
		findViewById(R.id.camera_snap_line).setVisibility(View.GONE);
		pictureButton.setVisibility(View.GONE);
		useButton.setVisibility(View.VISIBLE);
	}

	/**
	 * function: hideUseButton
	 *
     * Show the proper controls after the 'X' button is taken.
     *
     */
	private void hideUseButton() {
		findViewById(R.id.flash).setVisibility(View.VISIBLE);
		cameraFlashStatus.setVisibility(View.VISIBLE);
		findViewById(R.id.camera_snap_line).setVisibility(View.VISIBLE);
		pictureButton.setVisibility(View.VISIBLE);
		useButton.setVisibility(View.GONE);
		
		// Show the guides
		showGuides();
		
		// Clean up the memory
		if(bmp != null)
			bmp.recycle();
		
		if(croppedBitmap != null)
			croppedBitmap.recycle();
		
		bmp = null;
		croppedBitmap = null;
		pictureTaken = null;
	}

	
	/**
	 * function: initPreview
	 * 
     * Sets up the preview for the camera
     * 
     * @param  width     The width of the preview
     * @param height     The height of the preview
     * 
     */
	private void initPreview(int width, int height) {
		if (camera != null && holder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(holder);
			}
			catch (Throwable t) {
				camera.release();
				camera = null;
				return;
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters = camera.getParameters();
				DisplayMetrics metrics = getResources().getDisplayMetrics();
				int w = metrics.widthPixels;
				int h = metrics.heightPixels;
				Size previewSize = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
				Size pictureSize = getBestSupportedSize(parameters.getSupportedPictureSizes(), 1600, 1200);

				parameters.setPictureFormat(ImageFormat.JPEG);
				parameters.setJpegQuality(100);

				setFocus(parameters);

				if (previewSize!=null) {
					parameters.setPreviewSize(previewSize.width, previewSize.height);
					parameters.setPictureSize(pictureSize.width, pictureSize.height);
				}
				camera.setParameters(parameters);
				cameraConfigured = true;
			}
		}
	}

	/**
	 * function: startPreview
	 *
     * Starts the preview for the camera
     *
     */
	private void startPreview() {
		if (cameraConfigured && camera != null) {
			progressBar.setVisibility(View.INVISIBLE);
			camera.startPreview();
		}
	}
	
	/**
	 * callback: shutterCallback
	 * callback: pictureCallback
	 * 
     * Handle the camera functionality.
     * 
     */
	private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
		
		@Override
		public void onShutter() {
			if(camera == null) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
					camera = Camera.open(0);
				else
					camera = Camera.open();
			}
			progressBar.setVisibility(View.VISIBLE);
		}
	};
	
	private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			// If the camera was garbage-collected, re instantiate here
			if(camera == null) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
					camera = Camera.open(0);
				else
					camera = Camera.open();
			}
			
			camera.stopPreview();
			cameraSurfaceView.setVisibility(View.INVISIBLE);
			
			// Crop the image
			ImageView tl = (ImageView) findViewById(R.id.guide_tl);
			ImageView br = (ImageView) findViewById(R.id.guide_br);
			bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			
			int w = bmp.getWidth();
			int h = bmp.getHeight();
			int startX = 0;
			int startY = 0;
			int endY = 0;
			
			int actionBarHeight = dpToPx(36);
			TypedValue tv = new TypedValue();
			if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
				if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
					actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
			}
			
			if(w >= 1600) {
				startX = (int) ((double) (tl.getLeft()) * w / cameraSurfaceView.getWidth());
				startY = (int) ((double) (tl.getTop() + actionBarHeight) * h / cameraSurfaceView.getHeight());
				endY = (int) ((double) ((cameraSurfaceView.getHeight() - br.getBottom())) * h / cameraSurfaceView.getHeight());
				endY += dpToPx(40);
			}
			else {
				startX = (int) ((double) (tl.getLeft()) * w / cameraSurfaceView.getWidth());
				startY = (int) ((double) (tl.getTop()) * h / cameraSurfaceView.getHeight());
				endY = (int) ((double) ((cameraSurfaceView.getHeight() - br.getBottom())) * h / cameraSurfaceView.getHeight());
			}
			int newWidth = w - (startX * 2);
			int newHeight = h - (startY + endY);
			croppedBitmap = Bitmap.createBitmap(bmp, startX, startY, newWidth, newHeight);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
			pictureTaken = baos.toByteArray();
			
			// Set up the preview
			hideGuides();
			cameraPreviewImage.setImageBitmap(croppedBitmap);
			cameraPreviewImage.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
						
			data = null;
			
			if(bmp != null)
				bmp.recycle();
			
			bmp = null;
			baos = null;
			System.gc();
			showUseButton();
		}
	};
	
	/**
	 * function: getBestSupportedSize
	 * 
     * Get the best supported size for the container.
     * 
     * @return param:
     * Size object containing the correct height and width that most closely matched
     * the container's size
     * 
     */
	private Size getBestSupportedSize (List<Size> sizes, int width, int height) {
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Size s : sizes) {
			int area = s.width * s.height;
			if(s.width == width && s.height == height) {
				bestSize = s;
				break;
			}
			else if (area > largestArea) {
				bestSize = s;
				largestArea = area;
			}
		}
		return bestSize;
	}

	/**
	 * function: hideGuides
	 *
     * Hides the 4 crop guides and the text
     *
     *
     */
	public void hideGuides() {
		frontPhotoInstructions.setVisibility(View.GONE);
		backPhotoInstructions.setVisibility(View.GONE);
		guidelines_left.setVisibility(View.GONE);
		guidelines_right.setVisibility(View.GONE);
	}

	/**
	 * function:showGuides
	 *
     * Shows the 4 crop guides and the text
     *
     *
     */
	public void showGuides() {
		frontPhotoInstructions.setVisibility(View.VISIBLE);
		guidelines_left.setVisibility(View.VISIBLE);
		guidelines_right.setVisibility(View.VISIBLE);

		if(currentAction == BACK_PICTURE)
			backPhotoInstructions.setVisibility(View.VISIBLE);
		else
			backPhotoInstructions.setVisibility(View.GONE);
	}

	/**
	 * function: dpToPx
	 * 
     * Returns the pixels converted from dp.
     *
     * @param  dp     The dp value to convert
     *
     * @return param:
     * int representation of the dp converted to px
     *
     */
	public int dpToPx(int dp) {
	    DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
	    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	}

	private void setFocus (Camera.Parameters parameters) {
        if (parameters.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE); }
        else if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
    }


	/**
	 * callback: surfaceCallback
	 *
	 * Handle the camera surface functionality.
	 *
	 */

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		initPreview(width, height);
		startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		if(camera != null)
			camera.stopPreview();
	}
}
