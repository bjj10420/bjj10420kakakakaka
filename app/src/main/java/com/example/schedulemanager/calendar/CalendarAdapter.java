package com.example.schedulemanager.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.schedulemanager.R;
import com.example.schedulemanager.helper.DataHelper;
import com.example.schedulemanager.helper.EventHelper;
import com.example.schedulemanager.helper.UIHelper;
import com.example.schedulemanager.vo.Schedule;

import java.util.HashMap;
import java.util.List;

/**
 * 칼랜더 한페이지안의 각 한칸을 구성하는 어댑터
 */
public class CalendarAdapter extends BaseAdapter
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
	private HashMap<Integer, Schedule> scheduleMapForCurrentPage;



	public CalendarAdapter(Context context, Typeface typeface)
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
				convertView = mInlfater.inflate(R.layout.adapter_calendar_item, null);
				holder.day = (TextView) convertView.findViewById(R.id.calendar_item_day);
				holder.extra = (TextView) convertView.findViewById(R.id.calendar_item_extra);
				holder.checkMark = convertView.findViewById(R.id.check_mark);
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

			// 태그 첨부
			convertView.setTag(String.valueOf(item));

			// 첫번째 뷰는 rect존 생성을 위해 저장
			if(position == 0)
				EventHelper.eventHelper.getUiHelper().setFirstCalendarCell(convertView);


			if(item != null)
			{
				// 클릭 이벤트 설정
				convertView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								//TODO 리팩토링때 이벤트헬퍼로 가져가야 할 것
								actionClicked(v);
							}
				});
				// 롱 클릭 이벤트 설정
				convertView.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						actionLongClicked(v);
						return true;
					}
				});

				// 메인 저장소에 뷰 저장( 전달이나 다음달 제외 )
				if(!(position < firstItemIdx || position > lastItemIdx)) {
					DataHelper.dataHelper.getCurrentCalendarViewMap().put(item, convertView);
//					Log.d("캘린더어댑터 맵에 뷰 추가", "키 " + item + "으로 뷰를 추가하였습니다");
					
				}
				if(position == selectedItemIdx)
				{	// 선택된 날짜
					holder.day.setVisibility(View.VISIBLE);
					holder.extra.setVisibility(View.VISIBLE);

//					color = "ffffff";

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

					opacity = "";

					if(selectedTime != null && selectedTime.trim().isEmpty() == false)
					{
						extraText = selectedTime;
					}
					else
					{
						extraText ="지금";

					}

					// 이번달일때만
					if(DataHelper.dataHelper.getCurrentPageIndex() == 12)
					convertView.setBackgroundResource(R.drawable.calendar_item_selected_bg);
                    // 최초 한번만 오늘날짜의 달력 뷰 저장
					if(UIHelper.uiHelper.getViewOfToday() == null)
                    UIHelper.uiHelper.setViewOfToday(convertView);
					//TODO 시간표현은 필요없고 오늘날짜값을 저장
//					Log.d("오늘 날짜를 저장하였습니다.", String.valueOf(item));
					DataHelper.dataHelper.setDateOfToday(String.valueOf(item));
				}
				else
				{	// 선택되지 않은 날짜
					extraText = "";

					if(position == currentItemIdx)
					{	// 선택되지 않았지만 오늘 날짜라면 배경을 바꿔준다.
						convertView.setBackgroundResource(R.drawable.calendar_item_nonselected_now);
						convertView.setOnTouchListener(null);
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
						convertView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {


							}
						});

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
//
				// 날짜 색깔 및 텍스트 설정
				String textColor = "#" + opacity + color;
				holder.day.setTextColor(Color.parseColor(textColor));
				holder.day.setText(String.valueOf(item));
//
				// 스케쥴이 들어있는 날짜일경우
				// 키값이 "000" + 순서값 0
				if(isExistSchedule(item)) {
					holder.checkMark.setVisibility(View.VISIBLE);
					// 엑스트라 텍스트 설정
//					holder.extra.setText(extraText);
				}
			}//
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

	private void actionLongClicked(View v) {
		EventHelper.eventHelper.getCalendarHelper().getCalendarCellLongClickEvent(v);
	}

	public boolean isExistSchedule(Integer dateKey){
		boolean isExistSchedule = false;

			for(Integer key : scheduleMapForCurrentPage.keySet()) {
				Schedule schedule = scheduleMapForCurrentPage.get(key);
				String scheduleDate = schedule.getDate().substring(6, 8);
				Log.d("key 확인", "key =" + key);
				Log.d("dateKey 확인", "dateKey =" + dateKey);

				if(scheduleDate.equals(dateKey < 10 ? "0" + dateKey : String.valueOf(dateKey))){
						isExistSchedule = true;
						break;
				}
				}
		return isExistSchedule;
	}

	/**
	 * 클릭시 이벤트 처리
	 * @param v
     */
	private void actionClicked(View v) {
		EventHelper.eventHelper.getCalendarHelper().getCalendarCellClickEvent(v);
	}



	public class ViewHolder
	{
		public TextView		day;		// 날짜
		public TextView		extra;		// 하단 문구(시간이나 '지금' 문구 표시)
		public View			checkMark;	// 체크마크
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

	public void setScheduleMapForCurrentPage(HashMap<Integer, Schedule> scheduleMapForCurrentPage) {
		this.scheduleMapForCurrentPage = scheduleMapForCurrentPage;
	}

}
