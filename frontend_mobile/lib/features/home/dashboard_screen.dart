import 'package:flutter/material.dart';
import '../../core/services/api_service.dart';
import '../../core/theme/app_colors.dart';
import '../../core/widgets/glass_card.dart';
import 'ai_companion_screen.dart';

class DashboardScreen extends StatefulWidget {
  const DashboardScreen({super.key});

  @override
  State<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen> {
  late Future<dynamic> _dashboardFuture;

  @override
  void initState() {
    super.initState();
    _dashboardFuture = ApiService.getDashboardData();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Stack(
        children: [
          // Background - Pure White (as requested)
          Container(
            color: Colors.white,
          ),
          
          SafeArea(
            child: FutureBuilder<dynamic>(
              future: _dashboardFuture,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator(color: AppColors.primary));
                }

                if (snapshot.hasError) {
                  return Center(
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: GlassCard(
                        // Create a Blue Error Card
                        gradient: LinearGradient(
                          colors: [
                            AppColors.danger.withOpacity(0.9),
                            Colors.redAccent.withOpacity(0.7),
                          ],
                        ),
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                             const Icon(Icons.error_outline, size: 48, color: Colors.white),
                             const SizedBox(height: 16),
                             Text('Error: ${snapshot.error}', textAlign: TextAlign.center, style: const TextStyle(color: Colors.white)),
                             TextButton(
                               onPressed: () => setState(() => _dashboardFuture = ApiService.getDashboardData()),
                               child: const Text('Retry', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                             )
                          ],
                        ),
                      ),
                    ),
                  );
                }

                final data = snapshot.data ?? {};
                final userName = data['userName'] ?? 'User';
                final moodScore = data['currentMoodScore']?.toString() ?? 'N/A';
                final moodTrend = data['moodTrend']?.toString() ?? '0.0';
                final doctorName = data['doctorName'] ?? 'Not Assigned';

                return SingleChildScrollView(
                  padding: const EdgeInsets.all(24),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // Header
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Good Morning,',
                                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                                  color: AppColors.primaryDark.withOpacity(0.8),
                                ),
                              ),
                              Text(
                                userName,
                                style: Theme.of(context).textTheme.displaySmall?.copyWith(
                                  color: AppColors.primaryDark,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ],
                          ),
                          Container(
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              border: Border.all(color: AppColors.primary, width: 2),
                              boxShadow: [
                                BoxShadow(
                                  color: Colors.black.withOpacity(0.1),
                                  blurRadius: 8,
                                )
                              ]
                            ),
                            child: const CircleAvatar(
                              radius: 24,
                              backgroundImage: NetworkImage('https://i.pravatar.cc/150?img=12'),
                            ),
                          ),
                        ],
                      ),
                      
                      const SizedBox(height: 32),
                      
                      // Main Stat Card - Blue Glass
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
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                const Text(
                                  'Current Mood',
                                  style: TextStyle(color: Colors.white, fontSize: 16),
                                ),
                                Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                                  decoration: BoxDecoration(
                                    color: Colors.white.withOpacity(0.2), // Lighter pill inside blue card
                                    borderRadius: BorderRadius.circular(20),
                                  ),
                                  child: Row(
                                    children: [
                                      const Icon(Icons.show_chart, color: Colors.white, size: 16),
                                      const SizedBox(width: 4),
                                      Text('+$moodTrend', style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 12),
                            Text(
                              moodScore,
                              style: const TextStyle(
                                color: Colors.white,
                                fontSize: 56,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            const SizedBox(height: 8),
                            Text(
                              'Doctor: $doctorName',
                              style: const TextStyle(color: Colors.white70),
                            ),
                          ],
                        ),
                      ),
                      
                      const SizedBox(height: 24),
                      Text(
                        'Quick Actions',
                        style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          color: AppColors.primaryDark,
                          fontWeight: FontWeight.bold
                        ),
                      ),
                      const SizedBox(height: 16),
                      
                      Row(
                        children: [
                          Expanded(
                            child: GlassCard(
                              onTap: () {
                                Navigator.push(
                                  context,
                                  MaterialPageRoute(builder: (context) => const AiCompanionScreen()),
                                );
                              },
                              // Lighter Blue Glass Button
                              gradient: LinearGradient(
                                colors: [
                                  AppColors.primary.withOpacity(0.15),
                                  AppColors.accent.withOpacity(0.1),
                                ],
                              ),
                              child: const Column(
                                children: [
                                  Icon(Icons.mic, color: AppColors.primaryDark, size: 32),
                                  SizedBox(height: 8),
                                  Text('Talk to AI', style: TextStyle(color: AppColors.primaryDark, fontWeight: FontWeight.bold)),
                                ],
                              ),
                            ),
                          ),
                          const SizedBox(width: 16),
                          Expanded(
                            child: GlassCard(
                              onTap: () {
                                ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Go to Tests tab')));
                              },
                              gradient: LinearGradient(
                                colors: [
                                  AppColors.primary.withOpacity(0.15),
                                  AppColors.accent.withOpacity(0.1),
                                ],
                              ),
                              child: const Column(
                                children: [
                                  Icon(Icons.assignment, color: AppColors.primaryDark, size: 32),
                                  SizedBox(height: 8),
                                  Text('Start Test', style: TextStyle(color: AppColors.primaryDark, fontWeight: FontWeight.bold)),
                                ],
                              ),
                            ),
                          ),
                        ],
                      ),
                      
                      const SizedBox(height: 24),
                      Text(
                        'Recent Activity',
                        style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          color: AppColors.primaryDark,
                          fontWeight: FontWeight.bold
                        ),
                      ),
                      const SizedBox(height: 16),
                      GlassCard(
                        // Very Light Blue Glass for List Item
                        gradient: LinearGradient(
                          colors: [
                            AppColors.primary.withOpacity(0.1),
                            AppColors.accent.withOpacity(0.05),
                          ],
                        ),
                        child: ListTile(
                          contentPadding: EdgeInsets.zero,
                          leading: Container(
                            padding: const EdgeInsets.all(8),
                            decoration: BoxDecoration(
                              color: AppColors.primary.withOpacity(0.1),
                              shape: BoxShape.circle,
                            ),
                            child: const Icon(Icons.check, color: AppColors.primaryDark),
                          ),
                          title: const Text('Daily Check-in', style: TextStyle(color: AppColors.primaryDark, fontWeight: FontWeight.bold)),
                          subtitle: const Text('Completed yesterday', style: TextStyle(color: AppColors.textLight)),
                          trailing: const Text('+8.5', style: TextStyle(color: AppColors.primaryDark, fontWeight: FontWeight.bold)),
                        ),
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
