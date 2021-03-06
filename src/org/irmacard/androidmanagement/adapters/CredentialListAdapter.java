/**
 * CredentialListAdapter.java
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, Februari 2013.
 */

package org.irmacard.androidmanagement.adapters;

import java.util.ArrayList;
import java.util.List;

import org.irmacard.android.util.credentials.CredentialPackage;
import org.irmacard.androidmanagement.R;
import org.irmacard.credentials.info.CredentialDescription;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CredentialListAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;

	private List<CredentialPackage> credentials;

	public CredentialListAdapter(Activity activity,
			List<CredentialPackage> credentials) {
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (credentials != null) {
			this.credentials = credentials;
		} else {
			this.credentials = new ArrayList<CredentialPackage>();
		}
	}

	@Override
	public int getCount() {
		return credentials.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		return credentials.get(position).getCredentialDescription().getId();
	}

	@Override
	public View getView(int position, View convert_view, ViewGroup parent) {
		View view = convert_view;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item, null);
		}

		TextView name = (TextView) view
				.findViewById(R.id.item_label);

		CredentialDescription desc = credentials.get(position).getCredentialDescription();
		name.setText(desc.getShortName());
		return view;
	}

}
