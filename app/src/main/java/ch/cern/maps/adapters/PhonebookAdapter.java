package ch.cern.maps.adapters;

import java.util.ArrayList;

import ch.cern.maps.StartActivity;
import ch.cern.maps.models.Person;
import ch.cern.maps.utils.Constants;
import ch.cern.www.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PhonebookAdapter extends BaseAdapter {

	private Typeface typeface;
	private Context context;
	private ArrayList<Person> persons;

	public PhonebookAdapter(Context c, ArrayList<Person> p) {
		this.context = c;
		this.persons = p;
		this.typeface = Typeface.createFromAsset(context.getAssets(),
				"DroidSans.ttf");
	}

	@Override
	public int getCount() {
		return persons.size();
	}

	@Override
	public Object getItem(int i) {
		return persons.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(final int index, @Nullable View view, final ViewGroup parent) {

		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.person_item, parent, false);
		}

		final Person dataModel = persons.get(index);
		final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.personItem);
		final TextView personTitle = (TextView) view.findViewById(R.id.personTitle);
		personTitle.setTypeface(typeface);
		personTitle.setText(dataModel.getFamilyName() + ", " + dataModel.getFirstName());

		final TextView personDescription = (TextView) view.findViewById(R.id.personDescription);
		personDescription.setTypeface(typeface);
		personDescription.setText("Group: " + dataModel.getGroup() + "\nOffice: "
				+ dataModel.getOffice() + "\nEmail: " + dataModel.getEmail());
		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(context, StartActivity.class);
				i.putExtra(Constants.ScrollToTag, dataModel.getOffice());
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		});

		return view;
	}
}
