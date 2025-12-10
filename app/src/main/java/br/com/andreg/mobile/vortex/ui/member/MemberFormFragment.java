package br.com.andreg.mobile.vortex.ui.member;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.andreg.mobile.vortex.R;
import br.com.andreg.mobile.vortex.auth.SessionManager;
import br.com.andreg.mobile.vortex.model.Member;
import br.com.andreg.mobile.vortex.model.ScoutSession;
import br.com.andreg.mobile.vortex.BuildConfig;

public class MemberFormFragment extends Fragment {

    private static final String ARG_MEMBER_JSON = "ARG_MEMBER_JSON";
    private static final String ARG_EVENT_ID = "ARG_EVENT_ID";

    private EditText etName;
    private EditText etVisionId;
    private EditText etRegister;
    private Spinner spSession;
    private Button btSalvar;

    private Member memberToEdit;
    private String eventId;
    private RequestQueue requestQueue;
    private final Gson gson = new Gson();

    public MemberFormFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_form, container, false);

        this.etName = view.findViewById(R.id.etName);
        this.etVisionId = view.findViewById(R.id.etVisionId);
        this.etRegister = view.findViewById(R.id.etRegister);
        this.spSession = view.findViewById(R.id.spSession);
        this.btSalvar = view.findViewById(R.id.btSalvar);

        requestQueue = Volley.newRequestQueue(requireContext());

        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            String memberJson = getArguments().getString(ARG_MEMBER_JSON);

            if (memberJson != null && !memberJson.isEmpty()) {
                memberToEdit = gson.fromJson(memberJson, Member.class);
                populateFields(memberToEdit);
                btSalvar.setText("Salvar Alterações");
            } else {
                btSalvar.setText("Cadastrar Membro");
            }
        }

        fetchSessions();

        this.btSalvar.setOnClickListener(v -> onSubmit());

        return view;
    }

    private void populateFields(Member member) {
        etName.setText(member.getName());
        etVisionId.setText(member.getVisionId());
        etRegister.setText(member.getRegister());
    }

    private void fetchSessions() {
        String sessionsUrl = BuildConfig.BASE_URL + "/scout-sessions/";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, sessionsUrl, null,
                response -> {
                    try {
                        List<ScoutSession> sessions = gson.fromJson(response.toString(), new TypeToken<List<ScoutSession>>() {}.getType());

                        ArrayAdapter<ScoutSession> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sessions);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSession.setAdapter(adapter);

                        if (memberToEdit != null && memberToEdit.getSession() != null) {
                            for (int i = 0; i < sessions.size(); i++) {
                                if (sessions.get(i).getId().equals(memberToEdit.getSession().getId())) {
                                    spSession.setSelection(i);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("MemberForm", "Erro ao processar sessões", e);
                    }
                },
                error -> Toast.makeText(getContext(), "Erro ao buscar sessões: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = SessionManager.INSTANCE.getAuthToken();

                if (token != null) {
                    headers.put("Cookie", "__Secure-better-auth.session_token=" + token);
                    Log.d("MemberForm", "Enviando Cookie GET: " + token);
                }
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    private void onSubmit() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError("Nome obrigatório");
            return;
        }

        ScoutSession selectedSession = (ScoutSession) spSession.getSelectedItem();
        if (selectedSession == null) {
            Toast.makeText(getContext(), "Selecione uma seção", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", name);
            requestBody.put("visionId", etVisionId.getText().toString().trim());
            requestBody.put("register", etRegister.getText().toString().trim());
            requestBody.put("sessionId", selectedSession.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int method = Request.Method.POST;
        String url = BuildConfig.BASE_URL + "/event/" + eventId + "/members";

        Log.d("Url", url);

        if (memberToEdit != null) {
            method = Request.Method.PUT;
            url += "/" + memberToEdit.getId();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, requestBody,
                response -> {
                    Toast.makeText(getContext(), "Salvo com sucesso!", Toast.LENGTH_SHORT).show();

                    // Verifica se o listener foi configurado (conforme conversamos na pergunta anterior)
                    // Se não tiver listener, usa o back normal
//                    if (requireActivity() instanceof MemberFormFragment.OnMemberActionListener) {
//                        // Se você implementou a interface na Activity
//                    } else {
//                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
//                    }

//                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                },
                error -> {
                    String erro = "Erro ao salvar";

                    if (error.networkResponse != null && error.networkResponse.data != null) {

                        erro += ": " + new String(error.networkResponse.data, StandardCharsets.UTF_8);

                    }

                    Toast.makeText(getContext(), erro, Toast.LENGTH_LONG).show();

                    Log.e("MemberForm", "Erro API", error);

                    Log.e("MemberForm", "Erro API" + error.networkResponse);
                }
        ) {
            // --- CORREÇÃO AQUI ---
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    // Se o status for 201 ou 200 e não tiver dados, retornamos um JSON vazio fake
                    // para enganar o Volley e ele chamar o "onResponse" (sucesso)
                    if (response.data.length == 0) {
                        return Response.success(new JSONObject(), HttpHeaderParser.parseCacheHeaders(response));
                    }

                    // Se tiver dados, tenta fazer o parse padrão
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                    // Se a string for vazia ou null, retorna JSON vazio
                    if (jsonString == null || jsonString.trim().isEmpty()) {
                        return Response.success(new JSONObject(), HttpHeaderParser.parseCacheHeaders(response));
                    }

                    return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));

                } catch (Exception e) {
                    // Se der erro de parse, mas o status for de sucesso (200 ou 201),
                    // consideramos sucesso mesmo assim.
                    if (response.statusCode == 201 || response.statusCode == 200) {
                        return Response.success(new JSONObject(), HttpHeaderParser.parseCacheHeaders(response));
                    }
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                String token = SessionManager.INSTANCE.getAuthToken();
                if (token != null) {
                    headers.put("Cookie", "__Secure-better-auth.session_token=" + token);
                }
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}