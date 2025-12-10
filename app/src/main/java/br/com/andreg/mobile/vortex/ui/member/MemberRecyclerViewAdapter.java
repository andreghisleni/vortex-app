package br.com.andreg.mobile.vortex.ui.member;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.andreg.mobile.vortex.model.Member;
import br.com.andreg.mobile.vortex.databinding.FragmentGetMembersBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Member}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MemberRecyclerViewAdapter extends RecyclerView.Adapter<MemberRecyclerViewAdapter.ViewHolder> {

    private final List<Member> mValues;

    public MemberRecyclerViewAdapter(List<Member> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentGetMembersBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getRegister().length() < 2 ?"------------": mValues.get(position).getRegister());
        holder.mContentView.setText(mValues.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public Member mItem;

        public ViewHolder(FragmentGetMembersBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}