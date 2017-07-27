package com.example.schedulemanager.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.schedulemanager.R;

import java.util.List;

/**
 * 전체 시간표 조회 설정 시의 달력을 위한 베이스 어댑터(GridView)
 * @작성자 Hwang(mmuse1230@gmail.com)
 * @작성일 2016. 6. 19.
 * 
 */
public class BPLineRailCalendarAdapter extends BaseAdapter
{
	private Context					mContext;
	private Typeface				mTypeface;
	private LayoutInflater			mInlfater;
	
	private int						selectedItemIdx = -1;
	private int 					currentItemIdx = -1;
	private int						firstItemIdx;
	private int						lastItemIdx;
	private int 					visibleLastItemIdx = -1;
	private String					selectedTime;
	
	private List<Integer> 			calendarItems;
		
	public BPLineRailCalendarAdapter(Context context, Typeface typeface)
	{
		try
		{
			this.mContext = context;
			this.mTypeface = typeface;
			this.mInlfater = LayoutInflater.from(mContext);
		}
		catch (Exception e)
		{

		}		
	}
		
	public int getSelectedItemIdx()
	{
		return selectedItemIdx;
	}

	public void setSelectedItemIdx(int selectedItemIdx)
	{
		this.selectedItemIdx = selectedItemIdx;
	}

	public int getCurrentItemIdx()
	{
		return currentItemIdx;
	}

	public void setCurrentItemIdx(int currentItemIdx)
	{
		this.currentItemIdx = currentItemIdx;
	}

	public int getFirstItemIdx()
	{
		return firstItemIdx;
	}

	public void setFirstItemIdx(int firstItemIdx)
	{
		this.firstItemIdx = firstItemIdx;
	}

	public int getLastItemIdx()
	{
		return lastItemIdx;
	}

	public void setLastItemIdx(int lastItemIdx)
	{
		this.lastItemIdx = lastItemIdx;
	}
	
	public int getVisibleLastItemIdx()
	{
		return visibleLastItemIdx;
	}

	public void setVisibleLastItemIdx(int visibleLastItemIdx)
	{
		this.visibleLastItemIdx = visibleLastItemIdx;
	}

	public String getSelectedTime()
	{
		return selectedTime;
	}

	public void setSelectedTime(String selectedTime)
	{
		this.selectedTime = selectedTime;
	}

	public void setCalendarItems(List<Integer> calendarItems)
	{
		this.calendarItems = calendarItems;
	}

	@Override
	public int getCount()
	{
//		try
//		{
//			if(calendarItems != null)
//				return calendarItems.size();
//		}
//		catch (Exception e)
//		{
//			CommonUtils.printDebugStackTrace(e);
//		}
//		
//		return 0;
		
		return 42; 		// 6 * 7
	}

	@Override
	public Integer getItem(int position)
	{
		try
		{
			if(calendarItems != null)
				return calendarItems.get(position);
		}
		catch (Exception e)
		{
			//CommonUtils.printDebugStackTrace(e);
		}
		
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@SuppressLint({"InflateParams", "NewApi"})
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		
		try
		{
			if(convertView == null)
			{
				holder = new ViewHolder();
				convertView = mInlfater.inflate(R.layout.rail_adapter_calendar_item_view, null);
				
				holder.day = (TextView) convertView.findViewById(R.id.calendar_item_day);
				holder.extra = (TextView) convertView.findViewById(R.id.calendar_item_extra);
				
				holder.day.setTypeface(mTypeface, Typeface.BOLD);
				holder.extra.setTypeface(mTypeface, Typeface.BOLD);
				
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			
			Integer item = getItem(position);
			int mod = position % 7;
			
			String color = null;
			String opacity = null;			
			String extraText = null;
			
			if(item != null)
			{
				if(position == selectedItemIdx)
				{	// 선택된 날짜
					holder.day.setVisibility(View.VISIBLE);
					holder.extra.setVisibility(View.VISIBLE);
					
					color = "ffffff";
					opacity = "";
					
					if(selectedTime != null && selectedTime.trim().isEmpty() == false)
					{
						extraText = selectedTime;
					}
					else
					{
						
						extraText ="지금";
						
					}
					
					convertView.setBackgroundResource(R.drawable.rail_calendar_item_selected_bg);
					convertView.setOnTouchListener(null);
				}
				else
				{	// 선택되지 않은 날짜
					extraText = "";
					
					if(position == currentItemIdx)
					{	// 선택되지 않았지만 오늘 날짜라면 배경을 바꿔준다.
						convertView.setBackgroundResource(R.drawable.rail_calendar_item_nonselected_now);
					}
					else
					{	// 그 외의 경우는 배경을 없앤다.
						convertView.setBackground(null);
					}
					
					if(position < firstItemIdx || position > lastItemIdx)
					{	// 전달의 날짜거나, 다음달의 날짜라면 감춘다. 
						holder.day.setVisibility(View.INVISIBLE);
						holder.extra.setVisibility(View.INVISIBLE);
						
						convertView.setOnTouchListener(nullTouchListener);
						
						return convertView;
					}
					else
					{
						holder.day.setVisibility(View.VISIBLE);
						holder.extra.setVisibility(View.VISIBLE);

						// 요일에 따라서 색깔을 설정한다.
						switch (mod)
						{
							case 0:		// SUN
								color = "ff0000";
								break;
								
							case 6: 	// SAT
								color = "558ed5";
								break;
								
							default:	// MON~FRI
								color = "262626";
								break;
								
						}
						
						if((position < currentItemIdx) || (visibleLastItemIdx >= 0 && visibleLastItemIdx < position))
						{	
							// TODO 이번 달의 오늘 이전의 모든 날을 활성화시켜주기 위해 주석 처리
							// 오늘보다 작은 날짜의 경우는 반투명하게
							// 오늘보다 한 달을 넘어가는 경우도 반투명하게 처리한다.
							opacity = "";
							
							// 터치가 안 되게 막는다.
//							convertView.setOnTouchListener(nullTouchListener);
						}
						else
						{
							// 그 외의 경우는 opacity = 0%
							opacity = "";
							
							// 터치가 가능하게 null 터치 리스너를 해제해준다.
							convertView.setOnTouchListener(null);
						}
					}
				}
				
				// 날짜 색깔 및 텍스트 설정 
				String textColor = "#" + opacity + color;
				holder.day.setTextColor(Color.parseColor(textColor));
				holder.day.setText(String.valueOf(item));
				
				// 엑스트라 텍스트 설정
				holder.extra.setText(extraText);
			}
			else
			{	// 비어 있는 날짜
				holder.day.setVisibility(View.INVISIBLE);
				holder.extra.setVisibility(View.INVISIBLE);
				convertView.setOnTouchListener(nullTouchListener);
			}
		}
		catch (Exception e)
		{
		}
		
		return convertView;
	}
	
	public class ViewHolder
	{
		public TextView		day;		// 날짜
		public TextView		extra;		// 하단 문구(시간이나 '지금' 문구 표시)
	}
	
	/**
	 * 터치 이벤트 전파를 막기 위한 터치 리스너
	 */
	private OnTouchListener nullTouchListener = new OnTouchListener()
	{
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			return true;
		}
	};
}
