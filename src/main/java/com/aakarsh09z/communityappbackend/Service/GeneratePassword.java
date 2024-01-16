package com.aakarsh09z.communityappbackend.Service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
@Service
public class GeneratePassword {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "@#$%^&+=";
    public String generateRandomPassword() {
        List<Character> passwordCharacters = new ArrayList<>();
        passwordCharacters.add(getRandomCharacter(LOWERCASE));
        passwordCharacters.add(getRandomCharacter(UPPERCASE));
        passwordCharacters.add(getRandomCharacter(DIGITS));
        passwordCharacters.add(getRandomCharacter(SPECIAL_CHARACTERS));

        Random random = new Random();
        for (int i = passwordCharacters.size(); i < 10; i++) {
            String characterSet = getRandomCharacterSet();
            passwordCharacters.add(getRandomCharacterUsingRandom(characterSet, random));
        }
        Collections.shuffle(passwordCharacters);
        StringBuilder password = new StringBuilder();
        for (char character : passwordCharacters) {
            password.append(character);
        }
        return password.toString();
    }
    private static char getRandomCharacter(String characterSet) {
        Random random = new Random();
        int randomIndex = random.nextInt(characterSet.length());
        return characterSet.charAt(randomIndex);
    }
    private static char getRandomCharacterUsingRandom(String characterSet, Random random) {
        int randomIndex = random.nextInt(characterSet.length());
        return characterSet.charAt(randomIndex);
    }
    private static String getRandomCharacterSet() {
        String allCharacters = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;
        return allCharacters;
    }
}
