package br.com.andreg.mobile.vortex.ui.member;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.andreg.mobile.vortex.BuildConfig;
import br.com.andreg.mobile.vortex.R;
import br.com.andreg.mobile.vortex.auth.SessionManager;
import br.com.andreg.mobile.vortex.model.Member;

public class GetMembersFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    private static final String ARG_EVENT_ID = "ARG_EVENT_ID";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private ArrayList<Member> members;
    private RequestQueue requestQueue;
    private View view;
    private String eventId;
    private final Gson gson = new Gson();

    public GetMembersFragment() {}

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
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    // 1. Defina a interface para comunicação
    public interface OnMemberActionListener {
        void onNavigateAction();
    }

    private OnMemberActionListener actionListener;

    // 2. Método Setter para o Compose injetar a ação
    public void setOnMemberActionListener(OnMemberActionListener listener) {
        this.actionListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_get_members_list, container, false);

        this.requestQueue = Volley.newRequestQueue(requireContext());
        this.requestQueue.start();

        String url = BuildConfig.BASE_URL + "/event/" + eventId + "/members";
        Log.d("Url", url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                String token = SessionManager.INSTANCE.getAuthToken();
                if (token != null) {
                    headers.put("Cookie", "__Secure-better-auth.session_token=" + token);
                    Log.d("GetMembersFragment", "Enviando Cookie GET: " + token);
                }
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_member);
        fab.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onNavigateAction(); // <--- Chama o Compose aqui
            }
            Log.d("GetMembersFragment", "FloatingActionButton clicked");
        });

        return this.view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        FloatingActionButton fab = view.findViewById(R.id.fab_add_member);
//        fab.setOnClickListener(v -> {
//            GetMembersFragmentDirections.ActionGetMembersFragmentToMemberFormFragment action =
//                    GetMembersFragmentDirections.actionGetMembersFragmentToMemberFormFragment(eventId, null);
//            NavHostFragment.findNavController(this).navigate(action);
//        });
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        String erro = "Erro ao buscar";
        if (error.networkResponse != null && error.networkResponse.data != null) {
            erro += ": " + new String(error.networkResponse.data, StandardCharsets.UTF_8);
        }
        Toast.makeText(getContext(), erro, Toast.LENGTH_LONG).show();
        Log.e("GetMembersFragment", "Erro API", error);

        Snackbar.make(view, "Erro ao buscar membros!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response != null) {
                JSONArray data = response.getJSONArray("data");
                this.members = gson.fromJson(data.toString(), new TypeToken<List<Member>>() {}.getType());

                RecyclerView recyclerView = view.findViewById(R.id.list);
                Context context = view.getContext();

                if (mColumnCount <= 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                }
                recyclerView.setAdapter(new MemberRecyclerViewAdapter(this.members));

            } else {
                Snackbar.make(view, "A consulta não retornou nenhum registro!", Snackbar.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e("GetMembersFragment", "Erro de parsing no JSON", e);
            Snackbar.make(view, "Erro ao processar a resposta do servidor.", Snackbar.LENGTH_LONG).show();
        }
    }
}
