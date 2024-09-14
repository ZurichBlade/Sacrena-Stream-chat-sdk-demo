/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-chat-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.sacrenabymehuljadhav.streamcustomviews.message

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sacrenabymehuljadhav.ui.theme.myBackgroundColor
import com.example.sacrenabymehuljadhav.ui.theme.myForegroundColor
import io.getstream.chat.android.compose.R
import io.getstream.chat.android.compose.ui.theme.ChatTheme

/**
 * Custom input field that we use for our UI. It's fairly simple - shows a basic input with clipped
 * corners and a border stroke, with some extra padding on each side.
 *
 * Within it, we allow for custom decoration, so that the user can define what the input field looks like
 * when filled with content.
 *
 * @param value The current input value.
 * @param onValueChange Handler when the value changes as the user types.
 * @param modifier Modifier for styling.
 * @param enabled If the Composable is enabled for text input or not.
 * @param maxLines The number of lines that are allowed in the input, no limit by default.
 * @param border The [BorderStroke] that will appear around the input field.
 * @param innerPadding The padding inside the input field, around the label or input.
 * @param keyboardOptions The [KeyboardOptions] to be applied to the input.
 * @param decorationBox Composable function that represents the input field decoration as it's filled with content.
 */
@Composable
public fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    border: BorderStroke = BorderStroke(2.dp, Color.Gray),
    innerPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit,
) {
    var textState by remember { mutableStateOf(TextFieldValue(text = value)) }

    if (textState.text != value) {
        // Workaround to move cursor to the end after selecting a suggestion
        LaunchedEffect(value) {
            if (textState.text != value) {
                textState = textState.copy(
                    text = value,
                    selection = TextRange(value.length),
                )
            }
        }
    }

    val theme = ChatTheme.messageComposerTheme.inputField
    val typography = ChatTheme.typography
    val colors = ChatTheme.colors
    val description = stringResource(id = R.string.stream_compose_cd_message_input)

    BasicTextField(
        modifier = modifier
            .border(border = border, shape = theme.borderShape)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(myBackgroundColor)
            .padding(innerPadding)
            .semantics { contentDescription = description },
        value = textState,
        onValueChange = {
            textState = it
            if (value != it.text) {
                onValueChange(it.text)
            }
        },
        visualTransformation = {
            val styledText = buildAnnotatedMessageText(
                text = it.text,
                textColor = Color.White,
                textFontStyle = typography.body.fontStyle,
                linkColor = colors.primaryAccent,
            )
            TransformedText(styledText, OffsetMapping.Identity)
        },
        textStyle = com.example.sacrenabymehuljadhav.ui.theme.Typography.bodySmall.copy(fontSize = 16.sp),
        cursorBrush = SolidColor(myForegroundColor),
        decorationBox = { innerTextField -> decorationBox(innerTextField) },
        maxLines = maxLines,
        singleLine = maxLines == 1,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
    )
}

@Preview
@Composable
private fun InputFieldPreview() {
    ChatTheme {
        InputField(
            modifier = Modifier.fillMaxWidth(),
            value = "InputFieldPreview",
            onValueChange = { _ -> },
            decorationBox = { innerTextField -> innerTextField.invoke() },
        )
    }
}

@SuppressLint("RestrictedApi")
internal fun buildAnnotatedMessageText(
    text: String,
    textColor: Color,
    textFontStyle: FontStyle?,
    linkColor: Color,
    builder: (AnnotatedString.Builder).() -> Unit = {},
): AnnotatedString {
    return buildAnnotatedString {
        // First we add the whole text to the [AnnotatedString] and style it as a regular text.
        append(text)
        addStyle(
            SpanStyle(
                fontStyle = textFontStyle,
                color = textColor,
            ),
            start = 0,
            end = text.length,
        )

//        // Then for each available link in the text, we add a different style, to represent the links,
//        // as well as add a String annotation to it. This gives us the ability to open the URL on click.
//        linkify(
//            text = text,
//            tag = AnnotationTagUrl,
//            pattern = PatternsCompat.AUTOLINK_WEB_URL,
//            matchFilter = Linkify.sUrlMatchFilter,
//            schemes = URL_SCHEMES,
//            linkColor = linkColor,
//        )
//        linkify(
//            text = text,
//            tag = AnnotationTagEmail,
//            pattern = PatternsCompat.AUTOLINK_EMAIL_ADDRESS,
//            schemes = EMAIL_SCHEMES,
//            linkColor = linkColor,
//        )

        // Finally, we apply any additional styling that was passed in.
        builder(this)
    }
}