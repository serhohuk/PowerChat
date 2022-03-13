package com.serhohuk.powerchat.screen



import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.serhohuk.powerchat.R
import com.serhohuk.powerchat.data.SharedPrefsStorage.Companion.IS_LOGGED_IN_BOOL
import com.serhohuk.powerchat.screen.destinations.DialogScreenDestination
import com.serhohuk.powerchat.ui.theme.Shapes
import com.serhohuk.powerchat.viewmodel.MainViewModel
import org.koin.androidx.compose.viewModel


@Destination(start = true)
@Composable
fun LoginScreen(navigator : DestinationsNavigator){

    val context = LocalContext.current
    val viewModel : MainViewModel by viewModel<MainViewModel>()
    val isLoggedIn = remember{
        mutableStateOf(viewModel.getIsLoggedIn(IS_LOGGED_IN_BOOL))
    }
    val account  = remember {
        mutableStateOf(GoogleSignInAccount.createDefault())
    }


    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                val intent = result.data
                if (result.data != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(intent)
                    try {
                        account.value = task.getResult(ApiException::class.java)
                        isLoggedIn.value = true
                    } catch (e: ApiException) {
                        isLoggedIn.value = false
                    }
                    viewModel.saveBoolean(IS_LOGGED_IN_BOOL, isLoggedIn.value)
                }
            }
        }

    if (isLoggedIn.value) navigator.navigate(DialogScreenDestination(account.value))

    Column(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(id = R.color.main_color)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly

    ){
        Text(text = buildAnnotatedString {
            withStyle(style = SpanStyle(
                color = Color.Yellow,
                fontSize = 60.sp)
            ){
                append("P")
            }
            append("ower")
            withStyle(style = SpanStyle(
                color = Color.Blue,
                fontSize = 60.sp)
            ){
                append("C")
            }
            withStyle(style = SpanStyle(
                color = Color.Blue,
            )
            ){
                append("hat")
            }
        },
            fontSize = 40.sp,
            color = Color.Yellow,
            textAlign = TextAlign.Center,
            fontFamily =  FontFamily(Font(R.font.staatliches_regular)),
            fontStyle = FontStyle.Italic
        )
        SignInButton(
            text = "Sign in with Google",
            loadingText = "Signing in...",
            isLoading = isLoggedIn.value,
            shape= RoundedCornerShape(10.dp),
            icon = painterResource(id = R.drawable.ic_google),
            onClick = {
                startForResult.launch(getGoogleLoginAuth(context).signInIntent)

            })
    }
}

@Composable
fun SignInButton(
    text: String,
    loadingText: String = "Signing in...",
    icon: Painter,
    isLoading: Boolean = false,
    shape: Shape = Shapes.medium,
    borderColor: Color = LightGray,
    backgroundColor: Color = MaterialTheme.colors.surface,
    progressIndicatorColor: Color = MaterialTheme.colors.primary,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(
            enabled = !isLoading,
            onClick = onClick
        ),
        shape = shape,
        border = BorderStroke(width = 1.dp, color = borderColor),
        color = backgroundColor,
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = icon,
                contentDescription = "SignInButton",
                tint = Color.Unspecified,
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(text = if (isLoading) loadingText else text,
                fontSize = 18.sp)
            if (isLoading) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(16.dp)
                        .width(16.dp),
                    strokeWidth = 2.dp,
                    color = progressIndicatorColor
                )
            }
        }
    }
}

private fun getGoogleLoginAuth(context : Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestId()
        .requestProfile()
        .build()
    return GoogleSignIn.getClient(context, gso)
}