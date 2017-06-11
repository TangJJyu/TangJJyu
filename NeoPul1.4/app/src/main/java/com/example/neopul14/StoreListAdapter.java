package com.example.neopul14;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 리제 on 2017-05-09.
 */
public class StoreListAdapter{
    StoreListAdapter(){}

}

class ListViewItem {
    private Drawable flowerImage ;
    private String nameStr ;
    private String infoStr ;

    public void setFlowerImage(Drawable icon) {
        flowerImage = icon ;
    }
    public void setNameStr(String title) {
        nameStr = title ;
    }
    public void setInfoStr(String desc) {
        infoStr = desc ;
    }

    public Drawable getFlowerImage() {
        return this.flowerImage ;
    }
    public String getNameStr() {
        return this.nameStr ;
    }
    public String getInfoStr() {
        return this.infoStr ;
    }
}

class ListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;

    // ListViewAdapter의 생성자
    public ListViewAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_list, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.flowerImage) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.nameText) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.infoText) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getFlowerImage());
        titleTextView.setText(listViewItem.getNameStr());
        descTextView.setText(listViewItem.getInfoStr());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수
    public void addItem(Drawable icon, String title, String desc) {
        ListViewItem item = new ListViewItem();

        item.setFlowerImage(icon);
        item.setNameStr(title);
        item.setInfoStr(desc);

        listViewItemList.add(item);
    }
}
