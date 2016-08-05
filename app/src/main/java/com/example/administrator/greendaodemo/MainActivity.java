package com.example.administrator.greendaodemo;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.greenrobot.dao.query.Query;
import me.itangqi.greendao.DaoSession;
import me.itangqi.greendao.Note;
import me.itangqi.greendao.NoteDao;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    TextView tv_id;
    EditText et_name, et_age, et_score;
    Button btn_add, btn_delete, btn_update, btn_query;
    ListView lv_list;
    MyApplication myApplication;
    DaoSession daoSession;
    Cursor cursor;
    MyAdapter adapter;

    private Long id = 0L;
    /**
     * Not-null value.
     */
    private String age;
    private String name;
    private String score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApplication = (MyApplication) getApplicationContext();
        daoSession = myApplication.daoSession;


        initView();
        setAction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updata();
    }

    private void initView() {
        tv_id = (TextView) findViewById(R.id.tv_id);
        et_name = (EditText) findViewById(R.id.et_name);
        et_age = (EditText) findViewById(R.id.et_age);
        et_score = (EditText) findViewById(R.id.et_score);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_update = (Button) findViewById(R.id.btn_update);
        btn_query = (Button) findViewById(R.id.btn_query);
        lv_list = (ListView) findViewById(R.id.lv_list);

    }

    private void setAction() {
        btn_add.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_query.setOnClickListener(this);


        String orderBy = NoteDao.Properties.Id.columnName + " DESC";//根据Id降序排序
        //查询，得到cursor
        cursor = myApplication.getDb().query(daoSession.getNoteDao().getTablename(), daoSession.getNoteDao().getAllColumns(), null, null, null, null, orderBy);
        adapter = new MyAdapter(this, cursor);
        lv_list.setAdapter(adapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id_index) {
                id = cursor.getLong(0);
                tv_id.setText("id: " + id);
                et_name.setText(cursor.getString(1));
                et_age.setText(cursor.getInt(2) + "");
                et_score.setText(cursor.getDouble(3) + "");
            }
        });
    }

    private void updata(){
       adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {

        age = et_age.getText().toString();
        name = et_name.getText().toString();
        score = et_score.getText().toString();
        switch (view.getId()) {
            case R.id.btn_add:
                addEntity();
                break;
            case R.id.btn_delete:
                deleteEntity(id);
                break;
            case R.id.btn_update:
                updateList();
                break;
            case R.id.btn_query:
                query(name);
                break;
        }

    }

    /**
     * 添加
     */
    private void addEntity() {
        if (!TextUtils.isEmpty(name)) {
            Note entity = new Note(null, name, age, score);
            //面向对象添加表数据
            long result = daoSession.getNoteDao().insert(entity);
            Toast.makeText(MainActivity.this, result+"添加成功", Toast.LENGTH_SHORT).show();
            cursor.requery();//刷新
            //updata();
        } else {
            Toast.makeText(MainActivity.this, "name不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据id删除
     *
     * @param id
     */
    private void deleteEntity(long id) {
        daoSession.getNoteDao().deleteByKey(id);
        cursor.requery();
    }

    /**
     * 更新
     */
    private void updateList() {
        Note entity = new Note(id, name, age, score);
        daoSession.getNoteDao().update(entity);
        cursor.requery();
    }

    /**
     * 根据name查询
     *
     * @param name
     */
    private void query(String name) {
        if (!TextUtils.isEmpty(this.name)) {
            // Query 类代表了一个可以被重复执行的查询
            NoteDao dao = daoSession.getNoteDao();
            Query<Note> query = daoSession.getNoteDao().queryBuilder()
                    .where(NoteDao.Properties.Name.eq(this.name))
                    .orderAsc(NoteDao.Properties.Id)
                    .build();
            // 查询结果以 List 返回
            List<Note> count = query.list();
            Toast.makeText(MainActivity.this, count.size() + "条数据被查到", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "name不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    class MyAdapter extends CursorAdapter {


        public MyAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_db, parent, false);
            holder.tv_id = (TextView) view.findViewById(R.id.tv_id);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_age = (TextView) view.findViewById(R.id.tv_age);
            holder.tv_score = (TextView) view.findViewById(R.id.tv_score);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            long id = cursor.getLong(0);
            String name = cursor.getString(1);
            int age = cursor.getInt(2);
            double score = cursor.getDouble(3);
            holder.tv_id.setText(id + "");
            holder.tv_name.setText(name);
            holder.tv_age.setText(age + "");
            holder.tv_score.setText(score + "");
        }
    }

    static class ViewHolder {
        TextView tv_id;
        TextView tv_name;
        TextView tv_age;
        TextView tv_score;
    }
}
