/**
 * Rich, realistic clinical seed data for TMD Self-Care Demo Mode
 * Represents a patient on a 14-day recovery path for Temporomandibular Joint Disorder.
 */

// Helper to generate dates relative to today
const getRelativeDateStr = (daysAgo) => {
  const d = new Date()
  d.setDate(d.getDate() - daysAgo)
  return d.toLocaleDateString('en-GB', { day: '2-digit', month: '2-digit', year: 'numeric' }).replace(/\//g, '-')
}

const getRelativeTimestamp = (daysAgo, hour = 9, minute = 30) => {
  const d = new Date()
  d.setDate(d.getDate() - daysAgo)
  d.setHours(hour, minute, 0, 0)
  return d.getTime()
}

export const INITIAL_DEMO_USER = {
  id: 'demo-patient-001',
  name: 'Dr. Alex Morgan',
  email: 'demo.patient@tmdcare.com',
  date_of_birth: '1995-05-14',
  height_cm: '178',
  weight_kg: '72',
  user_metadata: {
    name: 'Dr. Alex Morgan',
    full_name: 'Dr. Alex Morgan',
    date_of_birth: '1995-05-14',
    height_cm: '178',
    weight_kg: '72'
  }
}

export const INITIAL_PAIN_RECORDS = [
  {
    id: 'pain-14',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(13),
    pain_level: 8,
    stress_level: 8,
    location: 'Left Jaw, Left Ear',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(13, 8, 30),
    created_at: new Date(getRelativeTimestamp(13, 8, 30)).toISOString()
  },
  {
    id: 'pain-13',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(12),
    pain_level: 7,
    stress_level: 7,
    location: 'Left Jaw, Head',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(12, 9, 15),
    created_at: new Date(getRelativeTimestamp(12, 9, 15)).toISOString()
  },
  {
    id: 'pain-12',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(11),
    pain_level: 7,
    stress_level: 8,
    location: 'Left Jaw, Right Jaw',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(11, 8, 45),
    created_at: new Date(getRelativeTimestamp(11, 8, 45)).toISOString()
  },
  {
    id: 'pain-11',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(10),
    pain_level: 6,
    stress_level: 6,
    location: 'Left Jaw',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(10, 10, 0),
    created_at: new Date(getRelativeTimestamp(10, 10, 0)).toISOString()
  },
  {
    id: 'pain-10',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(9),
    pain_level: 6,
    stress_level: 5,
    location: 'Left Jaw, Neck',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(9, 8, 20),
    created_at: new Date(getRelativeTimestamp(9, 8, 20)).toISOString()
  },
  {
    id: 'pain-9',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(8),
    pain_level: 5,
    stress_level: 6,
    location: 'Left Jaw',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(8, 9, 10),
    created_at: new Date(getRelativeTimestamp(8, 9, 10)).toISOString()
  },
  {
    id: 'pain-8',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(7),
    pain_level: 6,
    stress_level: 7,
    location: 'Left Jaw, Left Ear',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(7, 8, 45),
    created_at: new Date(getRelativeTimestamp(7, 8, 45)).toISOString()
  },
  {
    id: 'pain-7',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(6),
    pain_level: 5,
    stress_level: 5,
    location: 'Left Jaw',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(6, 9, 0),
    created_at: new Date(getRelativeTimestamp(6, 9, 0)).toISOString()
  },
  {
    id: 'pain-6',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(5),
    pain_level: 4,
    stress_level: 4,
    location: 'Left Jaw',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(5, 8, 30),
    created_at: new Date(getRelativeTimestamp(5, 8, 30)).toISOString()
  },
  {
    id: 'pain-5',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(4),
    pain_level: 4,
    stress_level: 4,
    location: 'Left Jaw, Chin',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(4, 9, 40),
    created_at: new Date(getRelativeTimestamp(4, 9, 40)).toISOString()
  },
  {
    id: 'pain-4',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(3),
    pain_level: 3,
    stress_level: 3,
    location: 'Left Jaw',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(3, 8, 15),
    created_at: new Date(getRelativeTimestamp(3, 8, 15)).toISOString()
  },
  {
    id: 'pain-3',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(2),
    pain_level: 3,
    stress_level: 4,
    location: 'Left Jaw',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(2, 9, 25),
    created_at: new Date(getRelativeTimestamp(2, 9, 25)).toISOString()
  },
  {
    id: 'pain-2',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(1),
    pain_level: 3,
    stress_level: 3,
    location: 'Left Jaw',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(1, 8, 50),
    created_at: new Date(getRelativeTimestamp(1, 8, 50)).toISOString()
  },
  {
    id: 'pain-1',
    user_id: 'demo-patient-001',
    date: getRelativeDateStr(0),
    pain_level: 2,
    stress_level: 2,
    location: 'Left Jaw',
    type: 'Map Log',
    timestamp: getRelativeTimestamp(0, 8, 10),
    created_at: new Date(getRelativeTimestamp(0, 8, 10)).toISOString()
  }
]

export const INITIAL_EXERCISE_RECORDS = [
  {
    id: 'ex-1',
    exercise_name: 'Diaphragmatic Breathing',
    duration_sec: 300,
    category: 'Relaxation',
    date: getRelativeDateStr(0),
    timestamp: getRelativeTimestamp(0, 9, 0),
    created_at: new Date(getRelativeTimestamp(0, 9, 0)).toISOString()
  },
  {
    id: 'ex-2',
    exercise_name: 'Warm Compress',
    duration_sec: 1200,
    category: 'Relaxation',
    date: getRelativeDateStr(0),
    timestamp: getRelativeTimestamp(0, 9, 30),
    created_at: new Date(getRelativeTimestamp(0, 9, 30)).toISOString()
  },
  {
    id: 'ex-3',
    exercise_name: 'Chin Tucks',
    duration_sec: 30,
    category: 'Mobility',
    date: getRelativeDateStr(1),
    timestamp: getRelativeTimestamp(1, 14, 0),
    created_at: new Date(getRelativeTimestamp(1, 14, 0)).toISOString()
  },
  {
    id: 'ex-4',
    exercise_name: 'Jaw Muscle Self-Massage',
    duration_sec: 120,
    category: 'Relaxation',
    date: getRelativeDateStr(1),
    timestamp: getRelativeTimestamp(1, 19, 30),
    created_at: new Date(getRelativeTimestamp(1, 19, 30)).toISOString()
  },
  {
    id: 'ex-5',
    exercise_name: 'Neck Side Stretch',
    duration_sec: 120,
    category: 'Stretching',
    date: getRelativeDateStr(2),
    timestamp: getRelativeTimestamp(2, 10, 15),
    created_at: new Date(getRelativeTimestamp(2, 10, 15)).toISOString()
  },
  {
    id: 'ex-6',
    exercise_name: 'Box Breathing',
    duration_sec: 120,
    category: 'Stress Relief',
    date: getRelativeDateStr(3),
    timestamp: getRelativeTimestamp(3, 11, 0),
    created_at: new Date(getRelativeTimestamp(3, 11, 0)).toISOString()
  },
  {
    id: 'ex-7',
    exercise_name: 'Controlled Mouth Opening',
    duration_sec: 30,
    category: 'Mobility',
    date: getRelativeDateStr(4),
    timestamp: getRelativeTimestamp(4, 15, 30),
    created_at: new Date(getRelativeTimestamp(4, 15, 30)).toISOString()
  },
  {
    id: 'ex-8',
    exercise_name: 'Guided Jaw Relaxation',
    duration_sec: 60,
    category: 'Relaxation',
    date: getRelativeDateStr(5),
    timestamp: getRelativeTimestamp(5, 20, 0),
    created_at: new Date(getRelativeTimestamp(5, 20, 0)).toISOString()
  },
  {
    id: 'ex-9',
    exercise_name: 'Shoulder Rolls',
    duration_sec: 60,
    category: 'Posture Relaxation',
    date: getRelativeDateStr(6),
    timestamp: getRelativeTimestamp(6, 16, 45),
    created_at: new Date(getRelativeTimestamp(6, 16, 45)).toISOString()
  },
  {
    id: 'ex-10',
    exercise_name: 'Diaphragmatic Breathing',
    duration_sec: 300,
    category: 'Relaxation',
    date: getRelativeDateStr(7),
    timestamp: getRelativeTimestamp(7, 9, 30),
    created_at: new Date(getRelativeTimestamp(7, 9, 30)).toISOString()
  }
]

export const INITIAL_SLEEP_RECORDS = [
  {
    id: 'sleep-1',
    date: getRelativeDateStr(0),
    sleep_hours: 7.8,
    sleep_quality: 'Good',
    jaw_clenching: false,
    morning_stiffness: 'None',
    wakeup_feeling: 'Refreshed',
    notes: 'Used night guard',
    timestamp: getRelativeTimestamp(0, 7, 30),
    created_at: new Date(getRelativeTimestamp(0, 7, 30)).toISOString()
  },
  {
    id: 'sleep-2',
    date: getRelativeDateStr(1),
    sleep_hours: 7.5,
    sleep_quality: 'Good',
    jaw_clenching: false,
    morning_stiffness: 'Mild',
    wakeup_feeling: 'Refreshed',
    notes: '',
    timestamp: getRelativeTimestamp(1, 7, 15),
    created_at: new Date(getRelativeTimestamp(1, 7, 15)).toISOString()
  },
  {
    id: 'sleep-3',
    date: getRelativeDateStr(2),
    sleep_hours: 7.0,
    sleep_quality: 'Good',
    jaw_clenching: false,
    morning_stiffness: 'None',
    wakeup_feeling: 'Refreshed',
    notes: '',
    timestamp: getRelativeTimestamp(2, 7, 45),
    created_at: new Date(getRelativeTimestamp(2, 7, 45)).toISOString()
  },
  {
    id: 'sleep-4',
    date: getRelativeDateStr(3),
    sleep_hours: 6.8,
    sleep_quality: 'Fair',
    jaw_clenching: true,
    morning_stiffness: 'Mild',
    wakeup_feeling: 'Tired',
    notes: 'Mild clenching',
    timestamp: getRelativeTimestamp(3, 7, 0),
    created_at: new Date(getRelativeTimestamp(3, 7, 0)).toISOString()
  },
  {
    id: 'sleep-5',
    date: getRelativeDateStr(4),
    sleep_hours: 8.0,
    sleep_quality: 'Excellent',
    jaw_clenching: false,
    morning_stiffness: 'None',
    wakeup_feeling: 'Refreshed',
    notes: 'Great rest',
    timestamp: getRelativeTimestamp(4, 8, 0),
    created_at: new Date(getRelativeTimestamp(4, 8, 0)).toISOString()
  },
  {
    id: 'sleep-6',
    date: getRelativeDateStr(5),
    sleep_hours: 7.2,
    sleep_quality: 'Good',
    jaw_clenching: false,
    morning_stiffness: 'Mild',
    wakeup_feeling: 'Refreshed',
    notes: '',
    timestamp: getRelativeTimestamp(5, 7, 30),
    created_at: new Date(getRelativeTimestamp(5, 7, 30)).toISOString()
  },
  {
    id: 'sleep-7',
    date: getRelativeDateStr(6),
    sleep_hours: 6.5,
    sleep_quality: 'Fair',
    jaw_clenching: true,
    morning_stiffness: 'Moderate',
    wakeup_feeling: 'Jaw Sore',
    notes: 'Stressful workday preceded',
    timestamp: getRelativeTimestamp(6, 6, 45),
    created_at: new Date(getRelativeTimestamp(6, 6, 45)).toISOString()
  }
]

export const INITIAL_WELLNESS_RECORDS = [
  {
    id: 'well-1',
    date: getRelativeDateStr(0),
    mood: '4', // 🙂
    notes: 'Work Stress, Clenching Jaw',
    timestamp: getRelativeTimestamp(0, 8, 45)
  },
  {
    id: 'well-2',
    date: getRelativeDateStr(1),
    mood: '4', // 🙂
    notes: 'None',
    timestamp: getRelativeTimestamp(1, 8, 30)
  },
  {
    id: 'well-3',
    date: getRelativeDateStr(2),
    mood: '3', // 😐
    notes: 'Work Stress',
    timestamp: getRelativeTimestamp(2, 9, 0)
  },
  {
    id: 'well-4',
    date: getRelativeDateStr(3),
    mood: '4', // 🙂
    notes: 'Anxiety',
    timestamp: getRelativeTimestamp(3, 8, 15)
  },
  {
    id: 'well-5',
    date: getRelativeDateStr(4),
    mood: '5', // 😄
    notes: 'None',
    timestamp: getRelativeTimestamp(4, 9, 30)
  }
]

/**
 * Intelligent AI Assistant Clinical Rule Engine
 */
export const getMockAiResponse = (userPrompt) => {
  const query = (userPrompt || '').toLowerCase()

  if (query.includes('reduce') || query.includes('pain') || query.includes('relief') || query.includes('jaw pain') || query.includes('hurt')) {
    return `### Clinical Recommendations for TMD Pain Relief

To effectively reduce acute Temporomandibular Joint pain, follow this multi-modal protocol:

1. **Thermal Therapy (Warm Compress)**
   - Apply a warm, moist towel to the masseter/temple area for 15-20 minutes up to 3 times daily.
   - Helps increase local circulation and release spastic masticatory muscles.

2. **Dietary Adjustments (Soft Food Diet)**
   - Avoid chewing hard, sticky, or chewy foods (e.g., tough meats, raw carrots, chewing gum).
   - Cut foods into smaller bites to minimize wide mandibular excursion.

3. **Jaw Resting Posture**
   - Keep your **"Lips together, teeth apart, tongue resting on the roof of your mouth"**.
   - Avoid resting your chin on your hands or cradling a phone between your shoulder and ear.

4. **Gentle Mobility**
   - Practice the **Controlled Mouth Opening** exercise in our Exercises tab.

> ⚠️ *Clinical Note: If pain is accompanied by sudden lockjaw, severe swelling, or fever, please seek immediate evaluation by an Orofacial Pain Specialist or ENT.*`
  }

  if (query.includes('exercise') || query.includes('routine') || query.includes('stretch') || query.includes('movement')) {
    return `### Recommended TMD Therapeutic Exercises

Targeted physical therapy stabilizes the temporomandibular joint and improves range of motion:

- **1. Controlled Mouth Opening (Mobility)**
  - Touch the tip of your tongue to the roof of your mouth right behind your front teeth.
  - Slowly lower your lower jaw until you feel a gentle stretch without discomfort. Hold 5 seconds.

- **2. Chin Tucks (Cervical & Posture Alignment)**
  - Sit tall, look straight ahead, and pull your chin straight backward as if making a double chin.
  - Aligns the cervical spine with the skull base, offloading anterior temporomandibular strain.

- **3. Masseter Self-Massage (Myofascial Release)**
  - Use circular index and middle finger pad pressure over the cheek muscles directly below the cheekbone.
  - Perform 1-2 minutes on each side before bedtime.

- **4. Diaphragmatic Breathing (Stress & Tone Reduction)**
  - Inhale for 4 seconds via the nose, exhale for 6 seconds via relaxed lips.

*Check the **Exercises** tab on the sidebar to follow along with our HD instructional video guides!*`
  }

  if (query.includes('stress') || query.includes('anxiety') || query.includes('clenching') || query.includes('grind') || query.includes('bruxism')) {
    return `### How Stress Impacts TMD & Bruxism

There is a direct correlation between autonomic nervous system arousal and nocturnal/daytime bruxism (clenching):

- **The Physiological Loop:** High cortisol and sympathetic tone cause subconscious contraction of the masseter and temporalis muscles.
- **Micro-Trauma:** Continuous clenching exerts up to 250 lbs of force per square inch across the articular disc, inducing joint inflammation and morning facial tension.

#### Suggested Interventions:
- 🌿 **Hourly Jaw Check-ins:** Set a gentle reminder to ensure your teeth are not in contact during screen work.
- 🧘 **Box Breathing Protocol:** Inhale 4s, Hold 4s, Exhale 4s, Hold 4s (available in Exercises).
- 🛡️ **Occlusal Splint (Night Guard):** Protects dentition and reduces joint loading if you grind during sleep.`
  }

  if (query.includes('sleep') || query.includes('pillow') || query.includes('position') || query.includes('night')) {
    return `### Sleep Optimization for TMD Recovery

Sleep quality directly impacts tissue repair and pain perception threshold:

1. **Optimal Sleep Posture**
   - **Back Sleeping (Supine):** The most biomechanically neutral position for the jaw and cervical spine.
   - **Cervical Pillow:** Use a pillow that supports the natural curve of the neck without flexing your head forward.
   - **Avoid Stomach Sleeping:** Sleeping face down forces the head to one side, placing continuous asymmetric torque on the TMJ.

2. **Pre-Sleep Relaxation Ritual**
   - 10 minutes of moist heat compress applied to bilateral jaw joints.
   - 5 minutes of guided diaphragmatic breathing to downregulate heart rate.

3. **Night Guard Usage**
   - Always rinse and wear your custom stabilization splint as prescribed by your dental specialist.`
  }

  if (query.includes('doctor') || query.includes('specialist') || query.includes('consult') || query.includes('dentist') || query.includes('hospital') || query.includes('emergency')) {
    return `### When to Consult an Orofacial Pain Specialist

While self-care routines manage most mild-to-moderate muscular TMD cases, professional intervention is indicated for:

- **🚨 Red Flag Symptoms:**
  - Inability to open or close your mouth (acute closed lock or open lock).
  - Severe, throbbing pain spreading to the ear, neck, or eye unaffected by NSAIDs or heat.
  - Visible facial asymmetry, unexplained swelling, or numbness.
  - History of recent facial or head trauma.

- **Recommended Specialists:**
  - **Board-Certified Orofacial Pain Specialist (ABOP)**
  - **Prosthodontist / TMJ Dental Specialist**
  - **Physical Therapist specializing in Craniofacial & TMJ Rehabilitation**

*You can generate and download a comprehensive clinical PDF summary from the **Reports** tab to share with your provider!*`
  }

  // General fallback medical assistant response
  return `### TMD Clinical Assistant Guidance

Thank you for your question regarding **${userPrompt || 'TMD Self-Care'}**.

Based on clinical guidelines for Temporomandibular Joint Disorders:

1. **Rest & Conservative Management:**
   - Maintain a neutral jaw position: keep your teeth apart and tongue resting comfortably on the palate.
   - Apply moist heat to relax tense masticatory muscles, or a cold pack for acute inflammation.

2. **Daily Routine Recommendations:**
   - Review the personalized **Exercises** program in your dashboard.
   - Log your pain levels regularly in the **Pain Map** to monitor recovery trajectories.

3. **Safety Disclaimer:**
   - This assistant provides evidence-based self-care guidance and is not a substitute for formal clinical diagnosis or emergency medical care.

*Would you like more details on specific jaw exercises, stress reduction techniques, or sleep hygiene?*`
}
