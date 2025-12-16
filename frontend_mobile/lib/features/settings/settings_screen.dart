import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/services/auth_service.dart';
import '../../core/services/theme_service.dart';
import '../../core/theme/app_colors.dart';
import '../../widgets/glass_card.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  bool _notificationsEnabled = true;

  @override
  Widget build(BuildContext context) {
    final authService = Provider.of<AuthService>(context);
    final themeService = Provider.of<ThemeService>(context);
    final user = authService.currentUser;

    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      body: Stack(
        children: [
          // Background - Pure White
          Container(
            color: Colors.white,
          ),
          
          SafeArea(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'Settings',
                    style: TextStyle(
                      fontSize: 32,
                      fontWeight: FontWeight.bold,
                      // color: AppColors.primaryDark, // Removed hardcoded
                    ),
                  ),
                  const SizedBox(height: 32),
                  
                  // Profile Card - Dark Blue Glass (White Text)
                  GlassCard(
                    opacity: 0.9,
                    gradient: LinearGradient(
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                      colors: [
                        AppColors.primary.withOpacity(0.85),
                        AppColors.accent.withOpacity(0.80),
                      ],
                    ),
                    child: Row(
                      children: [
                        Container(
                          decoration: BoxDecoration(
                            shape: BoxShape.circle,
                            border: Border.all(color: Colors.white, width: 2),
                          ),
                          child: const CircleAvatar(
                            radius: 30,
                            backgroundImage: NetworkImage('https://i.pravatar.cc/150?img=12'),
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                '${user?['firstName'] ?? 'User'} ${user?['lastName'] ?? ''}',
                                style: const TextStyle(
                                  color: Colors.white,
                                  fontSize: 20,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              Text(
                                user?['email'] ?? 'No Email',
                                style: const TextStyle(color: Colors.white70),
                              ),
                            ],
                          ),
                        ),
                        IconButton(
                          icon: const Icon(Icons.edit, color: Colors.white),
                          onPressed: () {
                             _showEditProfileDialog(context);
                          },
                        ),
                      ],
                    ),
                  ),
                  
                  const SizedBox(height: 24),
                  const Text(
                    'Preferences',
                    style: TextStyle(
                      color: AppColors.primaryDark,
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 16),
                  
                  // Preferences List - Light Blue Glass (Dark Text)
                  GlassCard(
                    padding: EdgeInsets.zero,
                    gradient: LinearGradient(
                      colors: [
                        AppColors.primary.withOpacity(0.1),
                        AppColors.accent.withOpacity(0.05),
                      ],
                    ),
                    child: Column(
                      children: [
                        _buildSettingItem(
                          context, 
                          'Notifications', 
                          Icons.notifications, 
                          switchValue: _notificationsEnabled,
                          onSwitchChanged: (val) {
                             setState(() => _notificationsEnabled = val);
                             ScaffoldMessenger.of(context).showSnackBar(
                               SnackBar(content: Text(val ? 'Notifications Enabled' : 'Notifications Disabled'))
                             );
                          }
                        ),
                        Divider(height: 1, color: AppColors.primary.withOpacity(0.1), indent: 56),
                        _buildSettingItem(
                          context, 
                          'Dark Mode', 
                          Icons.dark_mode, 
                          switchValue: themeService.isDarkMode,
                          onSwitchChanged: (val) => themeService.toggleTheme(),
                        ),
                        Divider(height: 1, color: AppColors.primary.withOpacity(0.1), indent: 56),
                        _buildSettingItem(
                          context, 
                          'Language', 
                          Icons.language, 
                          trailing: 'English',
                          onTap: () {
                            showModalBottomSheet(
                              context: context,
                              builder: (context) => _buildLanguageSheet(),
                            );
                          }
                        ),
                      ],
                    ),
                  ),
                  
                  const SizedBox(height: 24),
                  const Text(
                    'Account',
                    style: TextStyle(
                      color: AppColors.primaryDark,
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 16),
                  
                  // Account List - Light Blue Glass (Dark Text)
                  GlassCard(
                    padding: EdgeInsets.zero,
                    gradient: LinearGradient(
                      colors: [
                        AppColors.primary.withOpacity(0.1),
                        AppColors.accent.withOpacity(0.05),
                      ],
                    ),
                    child: Column(
                      children: [
                        _buildSettingItem(
                          context, 
                          'Change Password', 
                          Icons.lock,
                          onTap: () {
                            _showChangePasswordDialog(context);
                          }
                        ),
                        Divider(height: 1, color: AppColors.primary.withOpacity(0.1), indent: 56),
                        _buildSettingItem(
                          context, 
                          'Privacy Policy', 
                          Icons.privacy_tip,
                          onTap: () {
                             showDialog(
                               context: context,
                               builder: (context) => AlertDialog(
                                 title: const Text('Privacy Policy', style: TextStyle(color: AppColors.primaryDark)),
                                 content: const SingleChildScrollView(
                                   child: Text(
                                     '1. Introduction\n\n'
                                     'MyEmoHealth respects your privacy. This policy explains how we handle your personal health data.\n\n'
                                     '2. Data Collection\n\n'
                                     'We collect data you explicitly provide: voice recordings, quiz answers, and profile information.\n\n'
                                     '3. Data Usage\n\n'
                                     'Your data is used solely to provide mental health insights and connect you with your doctor.\n\n'
                                     '4. Security\n\n'
                                     'We use industry-standard encryption to protect your data.',
                                     style: TextStyle(height: 1.5, color: AppColors.textDark),
                                   ),
                                 ),
                                 actions: [
                                   TextButton(
                                     onPressed: () => Navigator.pop(context), 
                                     child: const Text('Close', style: TextStyle(color: AppColors.primary))
                                   )
                                 ],
                               ),
                             );
                          }
                        ),
                      ],
                    ),
                  ),
                  
                  const SizedBox(height: 32),
                  
                  // Logout Button - Red Gradient Glass
                  GlassCard(
                    gradient: LinearGradient(
                      colors: [
                        AppColors.danger.withOpacity(0.8),
                        Colors.redAccent.withOpacity(0.7),
                      ],
                    ),
                    onTap: () async {
                      // Confirm logout
                      final confirm = await showDialog<bool>(
                        context: context,
                        builder: (context) => AlertDialog(
                          title: const Text('Logout'),
                          content: const Text('Are you sure you want to logout?'),
                          actions: [
                            TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Cancel')),
                            TextButton(onPressed: () => Navigator.pop(context, true), child: const Text('Logout')),
                          ],
                        ),
                      );
                      
                      if (confirm == true) {
                        authService.logout();
                      }
                    },
                    child: const Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.logout, color: Colors.white),
                        SizedBox(width: 8),
                        Text(
                          'Log Out',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLanguageSheet() {
    return Container(
      padding: const EdgeInsets.all(24),
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Select Language', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: AppColors.primaryDark)),
          const SizedBox(height: 16),
          ListTile(
            title: const Text('English'),
            trailing: const Icon(Icons.check, color: AppColors.primary),
            onTap: () => Navigator.pop(context),
          ),
          ListTile(
            title: const Text('Français'),
            onTap: () {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Language changed to French')));
            },
          ),
          ListTile(
            title: const Text('Español'),
            onTap: () {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Language changed to Spanish')));
            },
          ),
        ],
      ),
    );
  }

  void _showChangePasswordDialog(BuildContext context) {
    final oldController = TextEditingController();
    final newController = TextEditingController();
    final confirmController = TextEditingController();
    final formKey = GlobalKey<FormState>();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: Theme.of(context).cardTheme.color,
        title: Text('Change Password', style: TextStyle(color: Theme.of(context).textTheme.bodyLarge?.color)),
        content: Form(
          key: formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextFormField(
                controller: oldController,
                obscureText: true,
                decoration: const InputDecoration(labelText: 'Old Password'),
                style: TextStyle(color: Theme.of(context).textTheme.bodyLarge?.color),
                validator: (val) => val!.isEmpty ? 'Required' : null,
              ),
              TextFormField(
                controller: newController,
                obscureText: true,
                decoration: const InputDecoration(labelText: 'New Password'),
                style: TextStyle(color: Theme.of(context).textTheme.bodyLarge?.color),
                validator: (val) => val!.length < 6 ? 'Min 6 chars' : null,
              ),
              TextFormField(
                controller: confirmController,
                obscureText: true,
                decoration: const InputDecoration(labelText: 'Confirm Password'),
                style: TextStyle(color: Theme.of(context).textTheme.bodyLarge?.color),
                validator: (val) => val != newController.text ? 'Passwords do not match' : null,
              ),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel', style: TextStyle(color: Colors.grey)),
          ),
          ElevatedButton(
            onPressed: () {
              if (formKey.currentState!.validate()) {
                // Mock Success
                Navigator.pop(context);
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('Password updated successfully!'),
                    backgroundColor: AppColors.success,
                  )
                );
              }
            },
            style: ElevatedButton.styleFrom(backgroundColor: AppColors.primary),
            child: const Text('Update'),
          ),
        ],
      ),
    );
  }

  void _showEditProfileDialog(BuildContext context) {
    final nameController = TextEditingController(text: "Marie Lefebvre");
    final emailController = TextEditingController(text: "patient1@test.com");
    final formKey = GlobalKey<FormState>();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: Theme.of(context).cardTheme.color,
        title: Text('Edit Profile', style: TextStyle(color: Theme.of(context).textTheme.bodyLarge?.color)),
        content: Form(
          key: formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextFormField(
                controller: nameController,
                decoration: const InputDecoration(labelText: 'Full Name'),
                style: TextStyle(color: Theme.of(context).textTheme.bodyLarge?.color),
                validator: (val) => val!.isEmpty ? 'Required' : null,
              ),
              TextFormField(
                controller: emailController,
                decoration: const InputDecoration(labelText: 'Email'),
                style: TextStyle(color: Theme.of(context).textTheme.bodyLarge?.color),
                validator: (val) => val!.contains('@') ? null : 'Invalid email',
              ),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel', style: TextStyle(color: Colors.grey)),
          ),
          ElevatedButton(
            onPressed: () {
              if (formKey.currentState!.validate()) {
                Navigator.pop(context);
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('Profile updated successfully!'),
                    backgroundColor: AppColors.success,
                  )
                );
              }
            },
            style: ElevatedButton.styleFrom(backgroundColor: AppColors.primary),
            child: const Text('Save'),
          ),
        ],
      ),
    );
  }

  Widget _buildSettingItem(
    BuildContext context, 
    String title, 
    IconData icon, 
    {
      bool? switchValue, 
      ValueChanged<bool>? onSwitchChanged,
      String? trailing,
      VoidCallback? onTap
    }
  ) {
    return ListTile(
      leading: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: AppColors.primary.withOpacity(0.1),
          shape: BoxShape.circle,
        ),
        child: Icon(icon, color: AppColors.primaryDark, size: 20),
      ),
      title: Text(title, style: const TextStyle(color: AppColors.primaryDark, fontWeight: FontWeight.w600)),
      trailing: switchValue != null
          ? Switch(
              value: switchValue,
              onChanged: onSwitchChanged,
              activeColor: AppColors.accent,
            )
          : trailing != null 
              ? Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text(trailing, style: const TextStyle(color: AppColors.textLight)),
                    const SizedBox(width: 8),
                    const Icon(Icons.arrow_forward_ios, color: AppColors.primaryDark, size: 16),
                  ],
                )
              : const Icon(Icons.arrow_forward_ios, color: AppColors.primaryDark, size: 16),
      onTap: onTap,
    );
  }
}
