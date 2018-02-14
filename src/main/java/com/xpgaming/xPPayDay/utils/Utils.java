package com.xpgaming.xPPayDay.utils;

import net.minecraft.util.text.TextFormatting;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Arrays;
import java.util.List;

public class Utils {
    // THANKS XPAND

    // Takes a config String, and changes any ampersands to section symbols, which we can use internally.
    public static String formatText(final String input)
    {
        // Set up a list of valid formatting codes.
        final List<Character> validFormattingCharacters = Arrays.asList
                (
                        // Color numbers.
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                        // Color letters, lower and upper case.
                        'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F',
                        // Other formatting codes.
                        'k', 'l', 'm', 'n', 'o', 'r'
                );

        // Start replacing our ampersands.
        final StringBuilder mutableInput = new StringBuilder(input);
        for (int i = 0; i < mutableInput.length(); i++)
        {
            // Is the character that's currently being checked an ampersand?
            if (mutableInput.charAt(i) == '&')
            {
                // Is the loop value lower than the total length of the input String? Let's not check out of bounds.
                if ((i + 1) < mutableInput.length())
                {
                    // Look ahead: Does the next character contain a known formatting character? Replace the ampersand!
                    if (validFormattingCharacters.contains(mutableInput.charAt(i + 1)))
                        mutableInput.setCharAt(i, '§');
                }
            }
        }

        // Replace our old input String with the one that we fixed formatting on.
        return mutableInput.toString();
    }

    // Takes a config String, and replaces a single placeholder with the proper replacement as many times as needed.
    public static String replacePlaceholder(final String input, final String placeholder, final String replacement)
    {
        // If our input has a placeholder inside, replace it with the provided replacement String. Case-insensitive.
        if (input.toLowerCase().contains(placeholder))
            return input.replaceAll("(?i)" + placeholder, replacement);
        else
            return input;
    }
}
