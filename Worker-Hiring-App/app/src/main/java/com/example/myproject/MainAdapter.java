package com.example.myproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MainAdapter extends FirebaseRecyclerAdapter<MainModel, MainAdapter.myViewHolder> {
        /**
         * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
         * {@link FirebaseRecyclerOptions} for configuration options.
         *
         * @param options
         */

        Button callBtn;

        public MainAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options) {
                super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull MainModel model) {
                holder.wname.setText(model.getWname());
                holder.wdesc.setText(model.getWdesc());
                holder.wreq.setText(model.getWreq());
                holder.wphone.setText(model.getWphone());
                holder.wemail.setText(model.getWemail());
                holder.wloc.setText(model.getWloc());
                holder.wsalary.setText(model.getWsalary());
                holder.wstartdate.setText(model.getWstartdate());
        }

        @NonNull
        @Override
        public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent, false);

//                TextView wphone;
//                wphone = view.findViewById(R.id.phone1);
//
//                public void callnow(View v){
//                        Intent intent = new Intent(Intent.ACTION_DIAL);
//                        intent.setData(Uri.parse("tel:" + wphone));
//                        context.startActivity(intent);
//                }
                return new myViewHolder(view);
        }

        class myViewHolder extends RecyclerView.ViewHolder{
                TextView wname, wemail,wdesc,wreq, wphone, wsalary, wstartdate, wloc ;

                public myViewHolder(@NonNull View itemView) {
                        super(itemView);
                        wname = itemView.findViewById(R.id.workname1);
                        wdesc = itemView.findViewById(R.id.workdesc1);
                        wreq = itemView.findViewById(R.id.workreq1);
                        wphone = itemView.findViewById(R.id.phone1);
                        wemail = itemView.findViewById(R.id.email1);
                        wloc = itemView.findViewById(R.id.workloc1);
                        wsalary = itemView.findViewById(R.id.worksal1);
                        wstartdate = itemView.findViewById(R.id.workdate1);
                }
        }
}
