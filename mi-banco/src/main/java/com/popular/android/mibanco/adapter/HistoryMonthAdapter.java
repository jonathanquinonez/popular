package com.popular.android.mibanco.adapter;

import android.R.color;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.foound.widget.AmazingAdapter;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.model.Month;
import com.popular.android.mibanco.util.FontChanger;

import java.util.List;

/**
 * Adapter class to manage data monthly history list
 */
public class HistoryMonthAdapter extends AmazingAdapter {

    private List<Pair<String, List<Month>>> months;
	private int monthsSize = 0;

    private final LayoutInflater inflater;

    private Month selectedMonth;
    
    private Context context;

    public HistoryMonthAdapter(final Context context, final List<Pair<String, List<Month>>> months, Month selectedMonth) {
        this.months = months;
		this.monthsSize = this.months.size();
        this.selectedMonth = selectedMonth;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getAmazingView(final int position, final View convertView, final ViewGroup parent) {
        View myConvertView = convertView;
        if (myConvertView == null) {
            myConvertView = inflater.inflate(R.layout.list_item_select_month, null);
        }

        final Month month = getItem(position);

        if (month != null) {
            ((TextView) myConvertView.findViewById(R.id.txt_month)).setText(month.getMonth());

            final RadioButton radioMonth = (RadioButton) myConvertView.findViewById(R.id.rb_month);
            if (selectedMonth != null && month.getValue().equalsIgnoreCase(selectedMonth.getValue())) {
                radioMonth.setChecked(true);
            } else {
                radioMonth.setChecked(false);
            }
            
			if (position == getCount() - 1) {
				myConvertView.findViewById(R.id.bottom_line).setVisibility(View.GONE);
			} else {
				myConvertView.findViewById(R.id.bottom_line).setVisibility(View.VISIBLE);
			}

            FontChanger.changeFonts(myConvertView);
        }

        return myConvertView;
    }
    
    @Override
	public void configurePinnedHeader(View headerLayout, int position, int alpha) {
		TextView sectionHeader = (TextView) headerLayout.findViewById(R.id.header_text);
		sectionHeader.setText(getSections()[getSectionForPosition(position)]);
		sectionHeader.setBackgroundColor(ContextCompat.getColor(context, color.transparent));
		sectionHeader.setTextColor(ContextCompat.getColor(context, R.color.white));
		FontChanger.changeFonts(sectionHeader);
	}

	@Override
	public int getPositionForSection(int section) {
		if (section < 0) {
			section = 0;
		}
		if (section >= monthsSize) {
			section = monthsSize - 1;
		}
		int c = 0;
		for (int i = 0; i < monthsSize; i++) {
			if (section == i) {
				return c;
			}
			c += months.get(i).second.size();
		}

		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		int c = 0;
		for (int i = 0; i < monthsSize; i++) {
			if (position >= c && position < c + months.get(i).second.size()) {
				return i;
			}
			c += months.get(i).second.size();
		}
		return -1;
	}

	@Override
	public String[] getSections() {
		String[] res = new String[monthsSize];
		for (int i = 0; i < monthsSize; i++) {
			res[i] = months.get(i).first;
		}
		return res;
	}
	@Override
	public int getCount() {
		int res = 0;
		for (int i = 0; i < monthsSize; i++) {
			res += months.get(i).second.size();
		}
		return res;
	}

	@Override
	public Month getItem(int position) {
		int c = 0;
		for (int i = 0; i < monthsSize; i++) {
			if (position >= c && position < c + months.get(i).second.size()) {
				return months.get(i).second.get(position - c);
			}
			c += months.get(i).second.size();
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected void onNextPageRequested(int page) {
	}

	@Override
	protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
		View headerBox = view.findViewById(R.id.header_box);
		TextView sectionTitle = (TextView) headerBox.findViewById(R.id.header_text);
		if (displaySectionHeader) {
			headerBox.setVisibility(View.VISIBLE);
			sectionTitle.setText(getSections()[getSectionForPosition(position)]);
		} else {
			headerBox.setVisibility(View.GONE);
		}
		FontChanger.changeFonts(view);
	}
}
