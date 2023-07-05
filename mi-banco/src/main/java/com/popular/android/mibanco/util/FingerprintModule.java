package com.popular.android.mibanco.util;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.widget.Toast;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.view.fragment.FingerprintAuthenticationDialogFragment;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

@TargetApi(23)
public class FingerprintModule {

    private static final String DIALOG_FRAGMENT_TAG = "fingerprintFragmentTag";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    static final String DEFAULT_KEY_NAME = "default_key";

    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;

    public FingerprintModule(Context context, FragmentManager fragmentManager){
        mContext = context;
        mFragmentManager = fragmentManager;
        fingerprintInit();
    }

    private void fingerprintInit()
    {
        if(AutoLoginUtils.osFingerprintRequirements(mContext,false)){
            try {
                mKeyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
                mKeyStore.load(null);
            } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException("Failed to get an instance of KeyStore", e);
            }

            try {
                mKeyGenerator = KeyGenerator
                        .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
            }
            Cipher defaultCipher;
            try {
                defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new RuntimeException("Failed to get an instance of Cipher", e);
            }

            KeyguardManager keyguardManager = mContext.getSystemService(KeyguardManager.class);
            FingerprintManager fingerprintManager = mContext.getSystemService(FingerprintManager.class);


            if (!keyguardManager.isKeyguardSecure()) {
                // Show a message that the user hasn't set up a fingerprint or lock screen.
                Toast.makeText(mContext,R.string.fp_no_setup,Toast.LENGTH_LONG).show();
                return;
            }

            // Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
            // See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
            // The line below prevents the false positive inspection from Android Studio
            // noinspection ResourceType
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // This happens when no fingerprints are registered.
                Toast.makeText(mContext,R.string.fp_no_fingerprint,Toast.LENGTH_LONG).show();
                return;
            }
            createKey(DEFAULT_KEY_NAME);
            createKey(KEY_NAME_NOT_INVALIDATED);
            displayFingerprintDialog(defaultCipher);
        }
    }


    /**
     * Initialize the {@link Cipher} instance with the created key in the
     * {@link #createKey(String)} method.
     *
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    private boolean initCipher(Cipher cipher) {
        try {
            if(mKeyStore.getKey(DEFAULT_KEY_NAME, null) instanceof SecretKey){
                SecretKey key = (SecretKey) mKeyStore.getKey(DEFAULT_KEY_NAME, null);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return true;
            }
            return false;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException | InvalidKeyException e) {
            if(mContext != null) {
                Toast.makeText(mContext, R.string.fp_no_fingerprint, Toast.LENGTH_LONG).show();
            }
            return false;
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     *
     * @param keyName the name of the key to be created
     *
     */
    public void createKey(String keyName) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (InvalidAlgorithmParameterException e) {
            if(mContext != null) {
                Toast.makeText(mContext, R.string.fp_no_fingerprint, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void displayFingerprintDialog(Cipher mCipher)
    {
        // Set up the crypto object for later. The object will be authenticated by use
        // of the fingerprint.
        if (initCipher(mCipher)) {

            // Show the fingerprint dialog. The user has the option to use the fingerprint with
            // crypto, or you can fall back to using a server-side verified password.
            FingerprintAuthenticationDialogFragment fragment
                    = new FingerprintAuthenticationDialogFragment();
            fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
            fragment.setStage(FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
            fragment.show(mFragmentManager, DIALOG_FRAGMENT_TAG);
        }

    }


}
