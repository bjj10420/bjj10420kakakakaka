package com.example.schedulemanager.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.example.schedulemanager.R;
import com.example.schedulemanager.helper.DataHelper;
import com.example.schedulemanager.helper.EventHelper;
import com.example.schedulemanager.vo.RectAndView;
import com.example.schedulemanager.vo.Schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 시간조회 달력 어댑터(뷰페이저)
 *
 * @author inhohwang(mmuse1230@gmail.com)
 * @date 2016. 7. 18.
 */
public class CalendarPagerAdapter extends PagerAdapter
{
	private ArrayList<ArrayList<Integer>> 	totalList;
	private Context							mContext;
	private Typeface						mTypeface;
	private LayoutInflater					mInflater;
	
	private SimpleDateFormat 				formatter;
	
	private Calendar						thisCal;
	private Calendar						nextCal;
	private Calendar						baseCal;

	private CalendarAdapter[] 				adapters;
	private SparseArray<View> 				views;
	
	private int								selectedYear		= -1;
	private int								selectedMonth		= -1;
	private int								selectedDay			= -1;
	private String							selectedTime		= null;
	private String							currentDate			= null;
	
	private OnCalendarItemClickListener		onCalendarItemClickListener;

	private HashMap<Integer, HashMap<Integer, Schedule>> scheduleMapByMonth;

	public CalendarPagerAdapter(Context context, Typeface typeface)
	{
		try
		{
			this.mContext = context;
			this.mTypeface = typeface;
			this.mInflater = LayoutInflater.from(mContext);

			/**
			 * 생성자에서 달력을 생성해준다
			 */
			thisCal = Calendar.getInstance(Locale.getDefault());
			adapters = new CalendarAdapter[25];
			views = new SparseArray<View>();

			// 모든 숫자리스트를 담는 저장소
			totalList = new ArrayList<ArrayList<Integer>>();

				for(int i = 0; i < 25; i++) {
					ArrayList<Integer> numberList = new ArrayList<Integer>();
					totalList.add(numberList);
				}

			formatter = new SimpleDateFormat("yyyy.  M", Locale.getDefault());
		}
		catch (Exception e)
		{
		}
	}
	
	public void setOnCalendarItemClickListener(OnCalendarItemClickListener l)
	{
		onCalendarItemClickListener = l;
	}

	/**
	 * 달력 초기화
	 *
	 * @author inhohwang(mmuse1230@gmail.com)
	 * @date 2016. 7. 18.
	 *
	 */
	public void initCalendar()
	{
		try
		{

			/**
			 * 이번달 달력 객체는 생성자에서 생성 ( NULL 포인트 에러 처리 )
			 */
			// 이번달 달력 객체
			thisCal = Calendar.getInstance(Locale.getDefault());

			// 이번달 달력(시작 달력)을 객체를 수정 => -12달
			// 1년전부터 시작되는 정보이므로
			thisCal.add(Calendar.MONTH, -12);

			// 다음달 달력 객체
			nextCal = Calendar.getInstance(Locale.getDefault());

			int year = thisCal.get(Calendar.YEAR);
			int month = thisCal.get(Calendar.MONTH);
			int date = thisCal.get(Calendar.DATE);

 			String curTime = String.format("%02d:%02d", thisCal.get(Calendar.HOUR_OF_DAY), thisCal.get(Calendar.MINUTE));
 			currentDate = String.format("%04d-%02d-%02d", thisCal.get(Calendar.YEAR), thisCal.get(Calendar.MONTH)+1, thisCal.get(Calendar.DATE));

			nextCal.set(year, month, date);
			nextCal.add(Calendar.MONTH, 1);

			// 변수 초기화
			selectedYear = -1;
			selectedMonth = -1;
			selectedDay = -1;
			selectedTime = curTime;

			// 어댑터 초기화(그리드뷰)
			for (int i = 0; i < adapters.length; i++) {
				if (adapters[i] != null) {
					adapters[i].setFirstItemIdx(0);
					adapters[i].setLastItemIdx(0);
					adapters[i].setCurrentItemIdx(-1);
					adapters[i].setSelectedItemIdx(-1);
					adapters[i].setSelectedTime(selectedTime);
				}
			}
		}
		catch (Exception e)
		{
		}
	}
	
	@Override
	public int getCount()
	{
		// 이번 달을 기준으로 1년전부터 1년후 데이터까지 생성 합계 25개
		return 25;
	}

	@Override
	public boolean isViewFromObject(View view, Object o)
	{
		return view == o;
	}
	
	@Override
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}
	
	@SuppressLint("InflateParams")
	public View instantiateItem(View pager, final int position)
	{
		View convertView = null;
		ViewHolder holder = null;

		try
		{
			convertView = views.get(position);
			Log.d("CalendarPagerAdapter instantiateItem", "체크 position = " + position);

			if(convertView == null)
			{
				convertView = mInflater.inflate(R.layout.rail_adapter_calendar, null);
				holder = new ViewHolder();
				
				holder.calendarGridView = (GridView) convertView.findViewById(R.id.timetable_param_setter_calendar_gridview);

				// 어댑터가 적용을 끝낸후의 리스너
				holder.calendarGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						DataHelper.dataHelper.makeRectZoneWithFirstCell(CalendarPagerAdapter.this);
//						DataHelper.dataHelper.sortRectZoneByKey();
//						callMatchRectZoneWithCurrentPageViewMap();
					}
				});
				CalendarAdapter adapter = new CalendarAdapter(mContext, mTypeface);
				adapters[position] = adapter;
				
				if(position == 0)
				{	// 1페이지 아이템 클릭 리스너
					holder.calendarGridView.setOnItemClickListener(page1ItemClickListener);
				}
				else
				{	// 2페이지 아이템 클릭 리스너
					holder.calendarGridView.setOnItemClickListener(page2ItemClickListener);
				}
				
				holder.calendarGridView.setAdapter(adapters[position]);


				convertView.setTag(holder);
				views.put(position, convertView);
			}

			// 메인액티비티의 뷰저장소 클리어
			DataHelper.dataHelper.getCurrentCalendarViewMap().clear();
			// 달력을 셋팅한다.
			setCalendar(position);
			// 페이저에 뷰를 붙인다.
			((ViewPager) pager).addView(convertView, 0);

		}
		catch (Exception e)
		{
		}
		
		return convertView;
	}

	public void callMatchRectZoneWithCurrentPageViewMap() {
		int currentPagerIndex = EventHelper.eventHelper.getCalendarHelper().getCalendarPager().getCurrentItem();
		DataHelper.dataHelper.matchRectZoneWithCurrentPageViewMap(adapters[currentPagerIndex].getFirstItemIdx());
	}

	@Override
	public void destroyItem(View container, int position, Object o)
	{
		try
		{
			View view = (View) o;
			((ViewPager) container).removeView(view);
			//views.remove(position);
		}
		catch (Exception e)
		{
		}
	}
	
	public class ViewHolder
	{
		public GridView		calendarGridView;	// 달력 그리드뷰
		public int 			firstItemIdx = 0;
		public int 			lastItemIdx = 0;
		public int 			currentItemIdx = -1;
		public int 			selectedItemIdx = -1;
	}
	
	/**
	 * 달력 셋팅
	 *
	 * @author inhohwang(mmuse1230@gmail.com)
	 * @date 2016. 7. 18.
	 *
	 * @param page		0: 이번달, 1: 다음달
	 */
	public void setCalendar(int page)
	{
		int firstItemIdx = 0;
		int lastItemIdx = 0;
		int visibleLastItemIdx = -1;
		int currentItemIdx = -1;
		int selectedItemIdx = -1;
		
		try
		{
			// 오늘 날짜를 기준으로 달력을 생성. 년과 월을 저장해둔다.
			int year = thisCal.get(Calendar.YEAR);
			int month = thisCal.get(Calendar.MONTH);
			int date = thisCal.get(Calendar.DATE);
			
			List<Integer> list = null;
			List<Integer> tempNumberList = null;
			Calendar base = null;
			Calendar next = null;
			CalendarAdapter adapter = null;
			
			if(page == 0)
			{
				base = thisCal;
				tempNumberList = totalList.get(0);
				tempNumberList.clear();
				list = tempNumberList;
				adapter = adapters[0];
			}

			else
			{
				base = Calendar.getInstance(Locale.getDefault());
				base.set(year, month, date);
				base.add(Calendar.MONTH, page);
				tempNumberList = totalList.get(page);
				tempNumberList.clear();
				list = tempNumberList;
				adapter = adapters[page];

				next = Calendar.getInstance(Locale.getDefault());
				next.set(year, month, date);
				next.add(Calendar.DAY_OF_MONTH, 30);
			}

			// 필드로 base값저장
			this.baseCal = base;

			// 연월값을 가지고 생성시에 받은 스케쥴 맵에 해당하는 내부스케쥴맵값을 가지고 온다.
			// base값을 이용
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM", Locale.getDefault());
			String baseYearMonthValue = formatter.format(new Date(base.getTimeInMillis()));
			HashMap<Integer, Schedule> scheduleHashMapForPage = scheduleMapByMonth.get(Integer.parseInt(baseYearMonthValue));
			// 해당 어댑터에 내부스케쥴맵값 설정
			adapter.setScheduleMapForCurrentPage(scheduleHashMapForPage);

			// 현재 보고 있는 달의 첫째 날과 마지막 날을 구한다.
			int firstDay = base.getActualMinimum(Calendar.DAY_OF_MONTH);
			int lastDay = base.getActualMaximum(Calendar.DAY_OF_MONTH);
			
			// 현재 보고 있는 달력을 기준으로 연과 월을 구한다.
			int currentYear = base.get(Calendar.YEAR);
			int currentMonth = base.get(Calendar.MONTH);
			
			// 첫째 날의 달력 생성
			Calendar calFirst = Calendar.getInstance();
			calFirst.set(currentYear, currentMonth, firstDay);
			
			// 마지막 날의 달력 생성
			Calendar calLast = Calendar.getInstance();
			calLast.set(currentYear, currentMonth, lastDay);
			
			// 첫째 날과 마지막 날의 요일을 구한다.
			int firstDayOfWeek = calFirst.get(Calendar.DAY_OF_WEEK);
			int lastDayOfWeek = calLast.get(Calendar.DAY_OF_WEEK);
			
			if(firstDayOfWeek != Calendar.SUNDAY)
			{	// 첫째 날이 일요일이 아니다. 달력에 표시될 이전 달의 날짜가 있다.
				Calendar calPrev = Calendar.getInstance();
				calPrev.set(currentYear, currentMonth, 1);
				calPrev.add(Calendar.MONTH, -1);		// 이전 달로 간다.
				
				// 이전 달의 마지막 날과 그 요일을 구한다.
				int lastDayPrev = calPrev.getActualMaximum(Calendar.DAY_OF_MONTH);
				calPrev.set(Calendar.DAY_OF_MONTH, lastDayPrev);
				int dow = calPrev.get(Calendar.DAY_OF_WEEK);
				
				// 모자란 날수만큼 채운다. 마지막날에서 하나씩 숫자를 빼면서 앞에서부터 리스트에 담는다.
				for(int i = 0; i < dow; i++)
				{
					list.add(0, lastDayPrev-i);
				}
			}
			
			// 첫째 날의 인덱스를 저장한다.
			firstItemIdx = list.size();
						
			// 이번 달의 날짜들을 리스트에 담는다.
			for(int i = firstDay; i <= lastDay; i++)
			{
				list.add(i);

				//TODO 수정이되면서 오늘이 선택되게 할때 고정 페이지(이번 달)와 인덱스로 판단하도록 바꿈 => 테스트 필요
				if(page == 12 && i == date)
				{	// 오늘 날짜의 인덱스를 저장한다.
					currentItemIdx = list.size()-1;
				}
				
				if(currentYear == selectedYear && currentMonth == selectedMonth && i == selectedDay)
				{	// 선택된 아이템이라면 인덱스를 저장한다.
					selectedItemIdx = list.size()-1;
				}


//				if(page == 1)
//				{	// 2페이지, 즉 다음달은 오늘에서 +30일까지만 보여준다.
//					if(i == next.get(Calendar.DAY_OF_MONTH))
//					{
//						visibleLastItemIdx = i;
//					}
//				}
			}
			
			// 마지막 날의 인덱스를 저장한다.
			lastItemIdx = list.size() - 1;
			
			if(lastDayOfWeek != Calendar.SATURDAY)
			{	// 마지막 날이 일요일이 아니다. 달력에 표시될 다음 달의 날짜가 있다.
				Calendar calNext = Calendar.getInstance();
				calNext.set(currentYear, currentMonth, 1);
				calNext.add(Calendar.MONTH, 1);		// 다음 달로 간다.
				
				// 다음 달의 첫째날과 그 요일을 구한다.
				int firstDayPrev = calNext.getActualMinimum(Calendar.DAY_OF_MONTH);
				int dow = calNext.get(Calendar.DAY_OF_WEEK);
				
				// 현재 요일부터 토요일까지 모자른 수만큼 채운다. 1일부터 루프가 돌아가는 수만큼 채워진다.
				for(int i = dow; i <= Calendar.SATURDAY; i++)
				{
					list.add(firstDayPrev++);
				}
			}
			
			if(selectedYear < 0 && selectedDay < 0 && selectedMonth < 0 && currentItemIdx >= 0)
			{	// 선택된 날짜가 없는데, 오늘 날짜가 포함된 달이라면, 오늘을 선택된 날로 해준다.
				selectedItemIdx = currentItemIdx;
			}	
			
			// ViewHolder에 인덱스 변수들을 설정해준다.
			adapter.setFirstItemIdx(firstItemIdx);
			adapter.setLastItemIdx(lastItemIdx);
			adapter.setCurrentItemIdx(currentItemIdx);
			adapter.setVisibleLastItemIdx(visibleLastItemIdx);
			adapter.setSelectedItemIdx(selectedItemIdx);
			adapter.setSelectedTime(selectedTime);
			
			// 어댑터에 달력의 숫자 목록을 설정해주고 변경사항을 통보
			adapter.setCalendarItems(list);
			adapter.notifyDataSetChanged();
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	 * 1페이지 클릭 리스너(이번 달)
	 */
	private OnItemClickListener page1ItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			try
			{
				int day = adapters[0].getItem(position);
				selectedYear = thisCal.get(Calendar.YEAR);
				selectedMonth = thisCal.get(Calendar.MONTH);
				selectedDay = day;
				
				// 아이템 클릭 리스너가 설정되어 있으면 실행시킨다.
				if(onCalendarItemClickListener != null)
					onCalendarItemClickListener.onItemClick(selectedYear, selectedMonth, selectedDay);
			}
			catch (Exception e)
			{
			}
		}
	};
	
	/**
	 * 2페이지 클릭 리스너(다음 달)
	 */
	private OnItemClickListener page2ItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			try
			{
				int day = adapters[1].getItem(position);
				selectedYear = nextCal.get(Calendar.YEAR);
				selectedMonth = nextCal.get(Calendar.MONTH);
				selectedDay = day;
				
				if(onCalendarItemClickListener != null)
					onCalendarItemClickListener.onItemClick(selectedYear, selectedMonth, selectedDay);
			}
			catch (Exception e)
			{
			}
		}
	};
	
	public String getDateString(int position)
	{
		try
		{
			if(position == 0)
			{
				return formatter.format(new Date(thisCal.getTimeInMillis()));
			}
			else
			{
				return formatter.format(new Date(nextCal.getTimeInMillis()));
			}
		}
		catch (Exception e)
		{
		}
		
		return null;
	}

	public int getSelectedYear()
	{
		return selectedYear;
	}

	public int getSelectedMonth()
	{
		return selectedMonth;
	}

	public int getSelectedDay()
	{
		return selectedDay;
	}

	/**
	 * 시간 설정
	 *
	 * @author inhohwang(mmuse1230@gmail.com)
	 * @date 2016. 7. 18.
	 *
	 * @param selectedTime
	 */
	public void setSelectedTime(String selectedTime)
	{
		this.selectedTime = selectedTime;
		
		try
		{
			// 시간이 설정되면 그리드뷰(달력 레이아웃)을 갱신해준다.
			// 두 개 모두 갱신해야 함.
			for(int i = 0; i < adapters.length; i++)
			{
				if(adapters[i] != null)
				{
					adapters[i].setSelectedTime(selectedTime);
				}
			}
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	 * 설정된 날짜를 가져온다.
	 *
	 * @author inhohwang(mmuse1230@gmail.com)
	 * @date 2016. 7. 18.
	 *
	 * @return 문자열(yyyy-MM-dd) 형태
	 */
	public String getSelectedDatetime()
	{
		try
		{
			if(selectedYear >= 0 && selectedMonth >= 0 && selectedDay > 0)
			{
				return String.format(Locale.getDefault(), "%d-%02d-%02d", selectedYear, (selectedMonth+1), selectedDay);
			}
		}
		catch (Exception e)
		{
		}
		
		return null;
	}
	
	/**
	 * 선택된 페이지(+미선택 페이지)를 갱신한다.
	 *
	 * @author inhohwang(mmuse1230@gmail.com)
	 * @date 2016. 7. 18.
	 *
	 * @param page
	 */
	public void setSelectedPage(int page)
	{
		try
		{
			// 페이지에 따라 달력 어댑터를 찾는다.
			CalendarAdapter adapter = adapters[page];
			
			int first = adapter.getFirstItemIdx();
			int last = adapter.getLastItemIdx();
			int selectedItemIdx = -1;
			for(int i = first; i <= last; i++)
			{
				// 선택된 날짜의 인덱스를 찾는다.
				int day = adapter.getItem(i);
				
				if(selectedDay == day)
				{
					selectedItemIdx = i;
					break;
				}
			}
			
			// 선택된 달력이 아닌 경우는 기존에 선택된 날짜가 있다면 제거
			if(page == 0)
			{
				adapters[1].setSelectedItemIdx(-1);
				adapters[1].notifyDataSetChanged();
			}
			else
			{
				adapters[0].setSelectedItemIdx(-1);
				adapters[0].notifyDataSetChanged();
			}
			
			// 선택된 인덱스를 찾았다면 해당 어댑터에 설정하고 갱신
			if(selectedItemIdx >= 0)
			{
				adapter.setSelectedItemIdx(selectedItemIdx);
				adapter.notifyDataSetChanged();
			}
			
		}
		catch (Exception e)
		{
		}
	}
	
	/*
	 * 뷰페이저 갱신 시에 하위 어댑터들도 함께 갱신한다(그리드뷰)
	 */
	@Override
	public void notifyDataSetChanged()
	{
		try
		{
			super.notifyDataSetChanged();
			
			for(int i = 0; i < adapters.length; i++)
			{
				if(adapters[i] != null)
				{
					adapters[i].notifyDataSetChanged();
				}
			}
		}
		catch (Exception e)
		{
		}
	}

	public void setScheduleMapByMonth(HashMap<Integer, HashMap<Integer, Schedule>> scheduleMapByMonth) {
		this.scheduleMapByMonth = scheduleMapByMonth;
	}

	/**
	 * @category getter
	 * @return the selectedTime
	 */
	public String getSelectedTime() {
		return selectedTime;
	}

	public Calendar getThisCal() {
		return thisCal;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public Calendar getNextCal() { return nextCal; }

	public Calendar getBaseCal() {
		return baseCal;
	}

	public CalendarAdapter[] getAdapters() {
		return adapters;
	}

	public SparseArray<View> getViews() {
		return views;
	}
}
