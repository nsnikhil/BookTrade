package com.trade.book.booktrade.fragments.introfragments;


import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.paolorotolo.appintro.ISlidePolicy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.trade.book.booktrade.R;
import com.trade.book.booktrade.fragments.dialogfragments.dialogFragmentLoading;
import com.trade.book.booktrade.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class intro2Fragment extends Fragment implements ISlidePolicy, View.OnClickListener {

    //private static final String mNullValue = "N/A";
    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;
    int RC_SIGN_IN = 540;
    boolean signedIn = false;

    //private CallbackManager callbackManager;
    //private AccessTokenTracker accessTokenTracker;
    //private ProfileTracker profileTracker;
    @BindView(R.id.intro2Text)
    TextView mSignInText;
    @BindView(R.id.sign_in_button)
    SignInButton signInButton;
    dialogFragmentLoading dialogFragmentLoading;
    //@BindView(R.id.login_button) LoginButton loginButton;
    private Unbinder mUnbinder;


    public intro2Fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_intro2, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        initilize();
        setGooglePlus();
        //FacebookSdk.sdkInitialize(getActivity());
        //setUpFblogin();
        return v;
    }


    private void initilize() {
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);
        //loginButton.setReadPermissions("public_profile");
        //loginButton.setReadPermissions();
        //callbackManager = CallbackManager.Factory.create();
    }

    /*private void setUpFblogin() {
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String profilePicUrl = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");
                                    SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    spf.edit().putString(getResources().getString(R.string.prefAccountId), String.valueOf(response.getJSONObject().getInt("id"))).apply();
                                    spf.edit().putString(getResources().getString(R.string.prefAccountName), response.getJSONObject().getString("name")).apply();
                                    new DownloadImage().execute(profilePicUrl);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,picture.width(600).height(600)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }*/

    private void setGooglePlus() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestId().requestEmail().requestProfile().build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.login_button:
                //setUpFblogin();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        signInIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        // callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                dialogFragmentLoading = new dialogFragmentLoading();
                dialogFragmentLoading.show(getFragmentManager(), "loading");
                preFetchValues(acct.getId(), acct);
            }
        }
    }

    private String buildBanUri(String accId) {
        String host = getResources().getString(R.string.urlServer);
        String queryUserName = getResources().getString(R.string.urlUserQuery);
        String url = host + queryUserName;
        String uidQuery = "uid";
        return Uri.parse(url).buildUpon().appendQueryParameter(uidQuery, accId).build().toString();
    }

    private void preFetchValues(String id, final GoogleSignInAccount acct) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, buildBanUri(id), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    checkBanned(response, acct);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void checkBanned(JSONArray array, GoogleSignInAccount acct) throws JSONException {
        if (array.length() > 0) {
            JSONObject object = array.getJSONObject(0);
            int banstatus = object.getInt("bstatus");
            if (banstatus == 0) {
                dialogFragmentLoading.dismiss();
                goForward(acct);
            }
            if (banstatus == 1) {
                dialogFragmentLoading.dismiss();
                chooseBanAction(acct);
            }
        } else {
            dialogFragmentLoading.dismiss();
            goForward(acct);
        }
    }

    private void chooseBanAction(final GoogleSignInAccount acct) {
        final AlertDialog.Builder choosePath = new AlertDialog.Builder(getActivity());
        choosePath.setTitle(getActivity().getResources().getString(R.string.singinBanned));
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        arrayAdapter.add("Send a request");
        arrayAdapter.add("Try different account");
        choosePath.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0) {
                    emailIntent(acct);
                }
                if (position == 1) {
                    signedIn = false;
                    choosePath.create().dismiss();
                }
            }
        });
        choosePath.create().show();
    }

    private void emailIntent(GoogleSignInAccount acct) {
        Intent request = new Intent(Intent.ACTION_SENDTO);
        request.putExtra(Intent.EXTRA_EMAIL, new String[]{"shelf.bee.corp@gmail.com"});
        request.putExtra(Intent.EXTRA_TEXT, "ShelfBee id : " + acct.getId() + "\n\n");
        request.setData(Uri.parse("mailto:"));
        if (request.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(request);
        } else {
            Toast.makeText(getActivity(), "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void goForward(GoogleSignInAccount acct) {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        spf.edit().putString(getResources().getString(R.string.prefAccountId), acct.getId()).apply();
        spf.edit().putString(getResources().getString(R.string.prefAccountName), acct.getDisplayName()).apply();
        if (acct.getPhotoUrl() != null) {
            downloadProfilePic(acct.getPhotoUrl().toString());
            closeSingInActivity();
        } else {
            closeSingInActivity();
        }
    }

    private void downloadProfilePic(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setDestinationUri(Uri.fromFile(new File(getActivity().getExternalCacheDir() + "/profile.jpg")));
        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }


    public void closeSingInActivity() {
        signedIn = true;
        mSignInText.setText(getActivity().getResources().getString(R.string.singin));
        signInButton.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Signed In", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isPolicyRespected() {
        return signedIn;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        Toast.makeText(getActivity(), "Sign In using any one service to continue", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
