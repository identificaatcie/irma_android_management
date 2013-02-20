package org.irmacard.androidmanagement;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.idemix.IdemixCredentials;
import org.irmacard.credentials.info.AttributeDescription;
import org.irmacard.credentials.info.InfoException;
import org.irmacard.credentials.info.IssuerDescription;

/**
 * A fragment representing a single Credential detail screen. This fragment is
 * either contained in a {@link CredentialListActivity} in two-pane mode (on
 * tablets) or a {@link CredentialDetailActivity} on handsets.
 */
public class CredentialDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	
	CredentialAttributeAdapter mAdapter;
	CredentialPackage credential;
	AndroidWalker aw;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CredentialDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// FIXME: this will not work in single screen mode
			ArrayList<CredentialPackage> credentials = ((CredentialListActivity) getActivity())
					.getCredentials();
			
			short cred_id = getArguments().getShort(
					CredentialDetailFragment.ARG_ITEM_ID);
			
			credential = null;
			for(CredentialPackage cp : credentials) {
				if(cp.getCredentialDescription().getId() == cred_id) {
					credential = cp;
				}
			}
		}
		
		aw = new AndroidWalker(getResources().getAssets());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_credential_detail,
				container, false);

		if (credential != null) {
			List<AttributeDescription> attr_desc = credential.getCredentialDescription().getAttributes();
			Attributes attr_vals = credential.getAttributes();
			mAdapter = new CredentialAttributeAdapter(getActivity(), attr_desc,
					attr_vals);
		}

		return rootView;
	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ListView list = (ListView) view
				.findViewById(R.id.detail_attribute_list);
		
		TextView issuerName = (TextView) view.findViewById(R.id.detail_issuer_description_name);
		TextView issuerAddress = (TextView) view.findViewById(R.id.detail_issuer_description_address);
		TextView issuerEMail = (TextView) view.findViewById(R.id.detail_issuer_description_email);
		TextView credentialDescription = (TextView) view.findViewById(R.id.detail_credential_desc_text);
		TextView validityValue = (TextView) view.findViewById(R.id.detail_validity_value);
		TextView validityRemaining = (TextView) view.findViewById(R.id.detail_validity_remaining);
		ImageView issuerLogo = (ImageView) view.findViewById(R.id.detail_issuer_logo);
		
		IssuerDescription issuer = credential.getCredentialDescription().getIssuerDescription();
		issuerName.setText(issuer.getName());
		issuerAddress.setText(issuer.getContactAddress());
		issuerEMail.setText(issuer.getContactEMail());
		
		// Display expiry, this should in fact be refactored
		Calendar expires = Calendar.getInstance();
		long expiry_epoch = new BigInteger(credential.getAttributes().get(
				"expiry")).longValue();
		expires.setTimeInMillis(expiry_epoch * IdemixCredentials.EXPIRY_FACTOR);
		if (Calendar.getInstance().after(expires)) {
			// Credential has expired
			validityValue.setText(R.string.credential_no_longer_valid);
			validityValue.setTextColor(getResources().getColor(R.color.irmared));
			validityRemaining.setText("");
		} else {
			// Credential still valid
			SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy");
			validityValue.setText(sdf.format(expires.getTime()));

			int deltaDays = (int) ((expires.getTime().getTime() - Calendar
					.getInstance().getTime().getTime())
					/ (1000 * 60 * 60 * 24));
			// FIXME: text should be from resources
			validityRemaining.setText(deltaDays + " days remaining");
		}
		
		credentialDescription.setText(credential.getCredentialDescription().getDescription());
		
		// Setting logo of issuer
		// FIXME: this should go via the API
		String issuerID = credential.getCredentialDescription().getIssuerID();
		Bitmap logo = null;
		try {
			logo = BitmapFactory.decodeStream(aw.retrieveFile(new URI(issuerID + "/logo.png")));
		} catch (InfoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(logo != null) {
			issuerLogo.setImageBitmap(logo);
		}

		list.setAdapter(mAdapter);
	}
}
