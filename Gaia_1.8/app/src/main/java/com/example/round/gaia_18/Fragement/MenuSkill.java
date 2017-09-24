package com.example.round.gaia_18.Fragement;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.round.gaia_18.Data.SkillData;
import com.example.round.gaia_18.Data.SkillInfo;
import com.example.round.gaia_18.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import android.os.Handler;
import android.widget.Toast;

import static com.example.round.gaia_18.MainActivity.dataBaseHelper;
import static com.example.round.gaia_18.MainActivity.dataList;
import static com.example.round.gaia_18.MainActivity.fruit;
import static com.example.round.gaia_18.MainActivity.mOverlayService;
import static com.example.round.gaia_18.MainActivity.relativeLayout;
import static com.example.round.gaia_18.MainActivity.seed;
import static com.example.round.gaia_18.R.id.linearLayout;

public class MenuSkill extends Fragment {

    private static final String TAG = ".ActiveSkillActivity";
    //skill을 띄어줄 list
    private ListView skillList;
    //skillList Adapter
    private SkillAdapter mAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //Fragement 화면 활성화
        View view = inflater.inflate(R.layout.menu_skill_fragment, container,false);

        mAdapter = new SkillAdapter(getContext(),R.layout.menu_skill_item);
        skillList = (ListView)view.findViewById(R.id.menuSkillList);
        skillList.setAdapter(mAdapter);

        return view;
    }

    public class SkillViewHolder{
        LinearLayout background;
        ImageView skillImage;
        ProgressBar skillExpBar;
        TextView skillLevel;
        TextView skillName;
        TextView coolTime;
        ImageButton skillUseButton;
        ImageButton skillLevelUp;
        TextView skillLevelUpScore;
        TextView skillExplain;
    }

    private class SkillAdapter extends ArrayAdapter<SkillInfo> {
        private LayoutInflater mInflater = null;

        public SkillAdapter(Context context, int resource) {
            super(context, resource);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() { return dataList.getSkillInfos().size(); }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            final SkillInfo skillInfo = dataList.getSkillInfos().get(position);
            final SkillData skillData = dataList.getSkillDatas().get(position);
            final SkillViewHolder skillViewHolder;

            if (skillInfo.getSkillView() == null) {

                Log.i("SkillViewHolder","skill ViewHolder Setting");
                view = mInflater.inflate(R.layout.menu_skill_item, parent, false);
                skillViewHolder = new SkillViewHolder();

                skillViewHolder.background = (LinearLayout) view.findViewById(R.id.skillBackground);
                skillViewHolder.skillImage = (ImageView) view.findViewById(R.id.skillImage);
                skillViewHolder.skillExpBar = (ProgressBar) view.findViewById(R.id.skillExp);
                skillViewHolder.skillLevel = (TextView) view.findViewById(R.id.skillLevel);
                skillViewHolder.skillName = (TextView) view.findViewById(R.id.skillName);
                skillViewHolder.coolTime = (TextView) view.findViewById(R.id.skillCoolTime);
                skillViewHolder.skillUseButton = (ImageButton) view.findViewById(R.id.skillUseButton);
                skillViewHolder.skillLevelUp = (ImageButton) view.findViewById(R.id.skillLevelUpButton);
                skillViewHolder.skillLevelUpScore = (TextView) view.findViewById(R.id.skillLevelUpScore);
                skillViewHolder.skillExplain = (TextView) view.findViewById(R.id.skillExplain);

                view.setTag(skillViewHolder);
                skillInfo.setSkillView(view);

            } else {
                Log.i("SkillViewHolder","skill ViewHolder Setting");
                skillViewHolder = (SkillViewHolder)skillInfo.getSkillView().getTag();
            }


            if (skillInfo != null && skillInfo.getSkillDataChange()) {

                skillInfo.setSkillDataChange(false);
                //후에 skill Image로 변경
                skillViewHolder.skillImage.setImageResource(R.drawable.image);
                skillViewHolder.skillName.setText(skillInfo.getSkillName());
                skillViewHolder.skillExpBar.setProgress(skillData.getSkillLevel() * 4);
                skillViewHolder.skillLevel.setText(Integer.toString(skillData.getSkillLevel()));
                int resourceId = getContext().getResources().getIdentifier("skillCase" + skillInfo.getSkillCase(), "string", getContext().getPackageName());
                skillViewHolder.skillExplain.setText(String.format(getString(resourceId), skillData.getSkillEffect()));

                if (skillData.getbuyType() == 1) {
                    skillViewHolder.skillLevelUp.setImageResource(R.drawable.fruit);
                } else {
                    skillViewHolder.skillLevelUp.setImageResource(R.drawable.seed);
                }

                //skill을 구입함.
                if (skillData.getSkillBuy()) {

                    //skill이 최대 레벨일 때
                    if (skillData.getSkillLevel() == skillInfo.getSkillMaxLevel()) {
                        skillViewHolder.skillLevelUp.setImageResource(R.drawable.complete);
                        skillViewHolder.background.setBackgroundResource(R.drawable.max_level_background);
                        skillViewHolder.skillLevelUpScore.setText("*MAX*");
                    } else {//skill이 최대 레벨이 아닐때
                        skillViewHolder.skillLevelUpScore.setText(dataList.getAllScore(skillData.getCost()));
                        skillViewHolder.background.setBackgroundResource(R.drawable.flower_buy_available);
                    }

                    //스킬이 cooltime으로 인해 사용이 불가한 경우
                    if (skillInfo.getSkillUseState()) {
                        skillViewHolder.coolTime.setVisibility(View.VISIBLE);
                        skillViewHolder.skillUseButton.setVisibility(View.INVISIBLE);
                        skillViewHolder.background.setBackgroundResource(R.drawable.flower_buy_item);
                    } else {
                        skillViewHolder.coolTime.setVisibility(View.INVISIBLE);
                        //지속스킬일 경우
                        if (skillInfo.getPassive() == 1) {
                            skillViewHolder.skillUseButton.setVisibility(View.INVISIBLE);
                        } else { //지속스킬이 아닐 경우
                            skillViewHolder.skillUseButton.setImageResource(R.drawable.use);
                            skillViewHolder.skillUseButton.setVisibility(View.VISIBLE);

                            skillViewHolder.skillUseButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    skillViewHolder.coolTime.setVisibility(View.VISIBLE);
                                    SkillCoolTimer timer = new SkillCoolTimer();
                                    timer.skillCoolTime(skillInfo, skillViewHolder.coolTime, skillViewHolder.skillUseButton, skillViewHolder.background);

                                    skillInfo.setSkillUseState(true);
                                    //Skill Cool Time동안은 사용 불가능
                                    skillViewHolder.skillUseButton.setVisibility(View.INVISIBLE);
                                    skillViewHolder.background.setBackgroundResource(R.drawable.flower_buy_item);

                                    useSkill(skillInfo.getSkillCase(), skillData.getSkillEffect());
                                }
                            });
                        }
                    }
                } else {//skill을 구입하지 않음.

                    skillViewHolder.skillUseButton.setVisibility(View.INVISIBLE);
                    skillViewHolder.background.setBackgroundResource(R.drawable.flower_item_lock);
                    skillViewHolder.skillUseButton.setVisibility(View.INVISIBLE);
                    skillViewHolder.skillLevelUpScore.setText(dataList.getAllScore(skillData.getCost()));
                }

                skillViewHolder.skillLevelUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.i("SkillLevelUp", "SkillData : " + skillData.getSkillNo() + " / Skill Buy Type : " + skillData.getbuyType());

                        if (skillData.getSkillLevel() + 1 > skillInfo.getSkillMaxLevel()) {
                            Toast.makeText(getActivity(), "최대 레벨입니다!!!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //게임 재화로 구입
                        if (skillData.getbuyType() == 2) {
                            if (buyForScore(skillData.getCost())) { //구입완료
                                dataList.replaceSkillData(skillData.getSkillNo(), skillData.getSkillLevel() + 1);
                                seed.setText(dataList.getAllScore(dataList.getScoreHashMap()));

                                skillInfo.setSkillDataChange(true);
                                mAdapter.notifyDataSetChanged();
                            } else { //재화부족으로 구매 실패
                                //이 부분은 좀 더 시각적으로 표현하자
                                Toast.makeText(getActivity(), "Score가 부족합니다!!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {//현금성 재화로 구입
                            if (buyForFruit(skillData.getCost())) {
                                dataList.replaceSkillData(skillData.getSkillNo(), skillData.getSkillLevel() + 1);
                                fruit.setText(dataList.getAllScore(dataList.getFruitHashMap()));

                                skillInfo.setSkillDataChange(true);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                //이 부분은 좀 더 시각적으로 표현하자
                                Toast.makeText(getActivity(), "Fruit이 부족합니다!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }

            return skillInfo.getSkillView();
        }
    }

    private class SkillCoolTimer{

        private final Handler handler= new Handler();
        Timer timer = new Timer();

        public void skillCoolTime(final SkillInfo skillInfo, final TextView coolTime, final ImageButton button,final LinearLayout background){

            final int time = skillInfo.getCoolTime();

            TimerTask task = new TimerTask() {
                int cool = 0;

                @Override
                public void run() {
                    cool++;
                    int newTime = time-cool;
                    int hour = newTime /3600;
                    newTime %= 3600;
                    int min = newTime / 60;
                    int sec = newTime % 60;
                    updateSec(hour,sec,min,coolTime,button,background);
                    if(sec == time) {
                        skillInfo.setSkillUseState(false);
                        timer.cancel();
                    }
                }
            };

            timer.schedule(task, 0,1000);
        }

        protected void updateSec(final int hour, final int sec, final int min, final TextView coolTime, final ImageButton button,final LinearLayout background){
            Runnable updater = new Runnable() {

                public void run() {
                    coolTime.setText(min+":"+sec);
                    if(min == 0 && sec == 0){
                        coolTime.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.VISIBLE);
                        background.setBackgroundResource(R.drawable.flower_buy_available);
                    }
                }

            };
            handler.post(updater);
        }
    }

    abstract class SkillUse{

        Handler handler= new Handler();
        Timer timer = new Timer();

        protected abstract void startSkill(final int skillType, final int time);
    }

    private class SkillUseTimer_type0 extends SkillUse{

        @Override
        public void startSkill(final int skillType, final int time){

            dataList.effectSkill(skillType);

            TimerTask task = new TimerTask() {
                int cool = 0;
                @Override
                public void run() {
                    cool++;
                    if(cool == time) {
                        Log.i("Time","Time Finished");
                        timer.cancel();
                        dataList.finishSkill(skillType);
                    }
                }
            };
            timer.schedule(task, 0,1000);
        }
    }

    private class SkillUseTimer_type2 extends SkillUse{

        @Override
        public void startSkill(final int skillType, final int time){

            TimerTask task = new TimerTask() {
                int cool = 0;
                @Override
                public void run() {
                    cool++;
                    autoClick();
                    if(cool == time*10) {
                        timer.cancel();
                    }
                }
            };
            timer.schedule(task, 0,100);
        }

        private void autoClick(){
            Runnable updater = new Runnable() {
                public void run() {
                    dataList.getClickView().performClick();
                }
            };

            handler.post(updater);
        }
    }

    private void useSkill(int skillType, int effect){

        switch (skillType){

            //스킬 유형1 : 일정 시간 얻는 점수 2배
            case 0:
                SkillUseTimer_type0 type0 = new SkillUseTimer_type0();
                type0.startSkill(skillType,effect);
                break;
            //스킬 유형2 : 점수 획득
            case 1:
                dataList.startSkill_type1(effect);
                seed.setText(dataList.getAllScore(dataList.getScoreHashMap()));
                mOverlayService.setSeed();
                break;
            //스킬 유형3 : 초당 10회 자동 탭
            case 2:
                SkillUseTimer_type2 type2 = new SkillUseTimer_type2();
                type2.startSkill(skillType,effect);
                break;
            //스킬 유형4 : 탭 당 점수 증가
//            case 3:break;
//            //스킬 유형5 : 분당 일정 획수 자동 탭
//            case 4:break;
            //스킬 유형6 : 날씨가 비 일시 일정량의 물 획득
            case 5:break;
        }
    }

    //게임 재화로 구매
    private Boolean buyForScore(ConcurrentHashMap<Integer, Integer> seed){

        Iterator<Integer> iterator = seed.keySet().iterator();

        while(iterator.hasNext()){
            int key = iterator.next();
            int value = seed.get(key);

            if(!dataList.minusScore(key,value,dataList.getScoreHashMap())){
                return false;
            }
        }
        return true;
    }

    //현금성 재화로 구매
    private Boolean buyForFruit(ConcurrentHashMap<Integer,Integer> fruit){

        Iterator<Integer> iterator = fruit.keySet().iterator();

        while(iterator.hasNext()){
            int key = iterator.next();
            int value = fruit.get(key);

            if(!dataList.minusScore(key,value,dataList.getFruitHashMap())){
                return false;
            }
        }

        return true;
    }
}