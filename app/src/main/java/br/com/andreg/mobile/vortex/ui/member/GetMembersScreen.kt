package br.com.andreg.mobile.vortex.ui.member


import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
// Importe o seu Fragmento Java aqui
import br.com.andreg.mobile.vortex.ui.member.MemberFormFragment

import android.view.ContextThemeWrapper
import br.com.andreg.mobile.vortex.ui.BlankFragment
import androidx.appcompat.R as AppCompatR // Alias para não confundir resources

@Composable
fun GetMembersScreen(
    eventId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Precisamos de um ID único para o container do fragmento,
    // remember garante que o ID não mude durante recomposições.
    val viewId = remember { View.generateViewId() }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            // --- A MÁGICA ESTÁ AQUI ---
            // Envolvemos o contexto em um Tema XML robusto (MaterialComponents ou AppCompat)
            // Isso garante que o Fragmento encontre todas as cores e estilos que precisa.
            val contextThemeWrapper = ContextThemeWrapper(
                ctx,
                br.com.andreg.mobile.vortex.R.style.Theme_Vortex_Xml_Bridge
                // Se não tiver criado o tema abaixo, use temporariamente:
                // androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar
            )

            FragmentContainerView(contextThemeWrapper).apply {
                id = viewId
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = { view ->
            val fragmentActivity = context as? FragmentActivity
            fragmentActivity?.let { activity ->
                // Verifica se o fragmento já foi adicionado para evitar duplicação
                val fragmentManager = activity.supportFragmentManager
                val existingFragment = fragmentManager.findFragmentById(view.id)

                if (existingFragment == null) {
                    val fragment = GetMembersFragment()
                    val args = Bundle().apply {
                        // AS CHAVES ABAIXO TEM QUE SER IGUAIS AS DO JAVA "ARG_..."
                        putString("ARG_EVENT_ID", eventId)
                    }
                    fragment.arguments = args

                    // Realiza a transação
                    fragmentManager.commit {
                        replace(view.id, fragment)
                        setReorderingAllowed(true)
                    }
                }
            }
        }
    )
}