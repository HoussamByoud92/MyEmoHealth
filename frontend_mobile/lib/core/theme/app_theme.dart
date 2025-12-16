import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'app_colors.dart';

class AppTheme {
  static ThemeData get lightTheme {
    return ThemeData(
      primaryColor: AppColors.primary,
      scaffoldBackgroundColor: AppColors.background,
      
      // Text Theme
      textTheme: GoogleFonts.interTextTheme().copyWith(
        displayLarge: const TextStyle(
          color: AppColors.primaryDark,
          fontWeight: FontWeight.bold,
          letterSpacing: -0.5,
        ),
        displayMedium: const TextStyle(
          color: AppColors.primaryDark,
          fontWeight: FontWeight.bold,
          letterSpacing: -0.5,
        ),
        headlineSmall: const TextStyle(
          color: AppColors.primaryDark,
          fontWeight: FontWeight.w700,
        ),
        bodyLarge: const TextStyle(
          color: AppColors.textDark,
        ),
        bodyMedium: const TextStyle(
          color: AppColors.textLight,
        ),
      ),
      
      // Color Scheme
      colorScheme: const ColorScheme.light(
        primary: AppColors.primary,
        secondary: AppColors.accent,
        surface: AppColors.white,
        background: AppColors.background,
        error: AppColors.danger,
      ),
      
      // App Bar Theme
      appBarTheme: const AppBarTheme(
        backgroundColor: Colors.transparent,
        elevation: 0,
        centerTitle: true,
        iconTheme: IconThemeData(color: AppColors.primaryDark),
        titleTextStyle: TextStyle(
          color: AppColors.primaryDark,
          fontSize: 18,
          fontWeight: FontWeight.w600,
        ),
      ),
      
      // Card Theme
      cardTheme: CardThemeData(
        color: AppColors.white,
        elevation: 8,
        shadowColor: Colors.black.withOpacity(0.05),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(20),
        ),
      ),
    );
  }

  static ThemeData get darkTheme {
    return ThemeData(
      brightness: Brightness.dark,
      primaryColor: AppColors.primary,
      scaffoldBackgroundColor: const Color(0xFF1A1A2E), // Dark Navy
      
      // Text Theme
      textTheme: GoogleFonts.interTextTheme(ThemeData.dark().textTheme).copyWith(
        displayLarge: const TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.bold,
          letterSpacing: -0.5,
        ),
        displayMedium: const TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.bold,
          letterSpacing: -0.5,
        ),
        headlineSmall: const TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.w700,
        ),
        bodyLarge: const TextStyle(
          color: Colors.white70,
        ),
        bodyMedium: const TextStyle(
          color: Colors.white54,
        ),
      ),
      
      // Color Scheme
      colorScheme: const ColorScheme.dark(
        primary: AppColors.primary,
        secondary: AppColors.accent,
        surface: Color(0xFF16213E), // Slightly lighter navy for cards
        background: Color(0xFF1A1A2E),
        error: AppColors.danger,
      ),
      
      // App Bar Theme
      appBarTheme: const AppBarTheme(
        backgroundColor: Colors.transparent,
        elevation: 0,
        centerTitle: true,
        iconTheme: IconThemeData(color: Colors.white),
        titleTextStyle: TextStyle(
          color: Colors.white,
          fontSize: 18,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }
}
