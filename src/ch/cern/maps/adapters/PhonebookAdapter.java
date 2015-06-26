package ch.cern.maps.adapters;

import java.util.ArrayList;

import ch.cern.maps.StartActivity;
import ch.cern.maps.models.Person;
import ch.cern.maps.utils.Constants;
import ch.cern.www.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PhonebookAdapter extends BaseAdapter {

	private Typeface mTypeface;
	private Context mContext;
	private ArrayList<Person> mPeople;
	private TextView tv;

	public PhonebookAdapter(Context c, ArrayList<Person> p) {
		this.mContext = c;
		this.mPeople = p;
		this.mTypeface = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans.ttf");
	}

	@Override
	public int getCount() {
		return mPeople.size();
	}

	@Override
	public Object getItem(int i) {
		return mPeople.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(final int index, View view, final ViewGroup parent) {

		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.person_item, parent, false);
		}

		final Person dataModel = mPeople.get(index);

		RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.personItem);
		tv = (TextView) view.findViewById(R.id.personTitle);
		tv.setTypeface(mTypeface);
		tv.setText(dataModel.getFamilyname() + ", " + dataModel.getFirstname());
		tv = (TextView) view.findViewById(R.id.personDescription);
		tv.setTypeface(mTypeface);
		tv.setText("Group: " + dataModel.getGroup() + "\nOffice: "
				+ dataModel.getOffice() + "\nEmail: " + dataModel.getEmail());
		rl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(mContext, StartActivity.class);
				i.putExtra(Constants.ScrollToTag, dataModel.getOffice());
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(i);
			}
		});

		return view;
	}
}
