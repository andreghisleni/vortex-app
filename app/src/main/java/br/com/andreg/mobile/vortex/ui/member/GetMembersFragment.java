package br.com.andreg.mobile.vortex.ui.member;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import java.util.ArrayList;

import br.com.andreg.mobile.vortex.R;
import br.com.andreg.mobile.vortex.model.Member;

/**
 * A fragment representing a list of Items.
 */
public class GetMembersFragment extends Fragment implements Response.ErrorListener, Response.Listener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private ArrayList<Member> members;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayReq;
    private View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GetMembersFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GetMembersFragment newInstance(int columnCount) {
        GetMembersFragment fragment = new GetMembersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_get_members_list, container, false);


        return view;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(Object o) {

    }
}