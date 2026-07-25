-- Enable UUID extension if not already enabled
create extension if not exists "uuid-ossp";

-- 1. Create Public Users Table (Linked to Supabase Auth)
create table public.users (
  id uuid references auth.users on delete cascade not null primary key,
  name text not null,
  email text not null,
  auth_provider text not null,
  profile_image_path text,
  height_cm real,
  weight_kg real,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null
);

-- Turn on Row Level Security for users
alter table public.users enable row level security;
create policy "Users can view their own profile." on public.users for select using (auth.uid() = id);
create policy "Users can update their own profile." on public.users for update using (auth.uid() = id);

-- Trigger to automatically create a public user when an auth user signs up
create or replace function public.handle_new_user()
returns trigger as $$
begin
  insert into public.users (id, name, email, auth_provider)
  values (new.id, coalesce(new.raw_user_meta_data->>'full_name', new.email), new.email, 'google');
  return new;
end;
$$ language plpgsql security definer;

create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();

-- 2. Pain Records
create table public.pain_records (
  id uuid default uuid_generate_v4() primary key,
  user_id uuid references public.users(id) on delete cascade not null,
  date text not null,
  pain_level integer not null,
  stress_level integer not null,
  location text not null,
  type text default 'Dull',
  timestamp bigint not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null
);
alter table public.pain_records enable row level security;
create policy "Users can manage own pain records" on public.pain_records for all using (auth.uid() = user_id);

-- 3. Sleep Records
create table public.sleep_records (
  id uuid default uuid_generate_v4() primary key,
  user_id uuid references public.users(id) on delete cascade not null,
  date text not null,
  sleep_hours real not null,
  sleep_quality text not null,
  jaw_clenching boolean not null,
  morning_stiffness text not null,
  wakeup_feeling text not null,
  notes text,
  timestamp bigint not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null
);
alter table public.sleep_records enable row level security;
create policy "Users can manage own sleep records" on public.sleep_records for all using (auth.uid() = user_id);

-- 4. Exercise Records
create table public.exercise_records (
  id uuid default uuid_generate_v4() primary key,
  user_id uuid references public.users(id) on delete cascade not null,
  date text not null,
  exercise_name text not null,
  duration_sec integer not null,
  category text not null,
  timestamp bigint not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null
);
alter table public.exercise_records enable row level security;
create policy "Users can manage own exercise records" on public.exercise_records for all using (auth.uid() = user_id);

-- 5. Wellness Records
create table public.wellness_records (
  id uuid default uuid_generate_v4() primary key,
  user_id uuid references public.users(id) on delete cascade not null,
  date text not null,
  sleep_quality text not null,
  jaw_stiffness text not null,
  teeth_grinding boolean not null,
  mood text not null,
  water_intake integer not null,
  energy_level integer not null,
  notes text,
  timestamp bigint not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null
);
alter table public.wellness_records enable row level security;
create policy "Users can manage own wellness records" on public.wellness_records for all using (auth.uid() = user_id);

-- 6. Assessment Records
create table public.assessment_records (
  id uuid default uuid_generate_v4() primary key,
  user_id uuid references public.users(id) on delete cascade not null,
  timestamp bigint not null,
  date text not null,
  q1_teeth_grinding boolean not null,
  q2_jaw_clenching boolean not null,
  q3_chew_gum boolean not null,
  q4_bite_nails boolean not null,
  q5_jaw_clicking boolean not null,
  q6_difficulty_chewing boolean not null,
  q7_morning_stiffness boolean not null,
  q8_frequent_headaches boolean not null,
  q9_sleep_less_than_6_hours boolean not null,
  q10_high_stress boolean not null,
  q11_poor_posture boolean not null,
  q12_one_side_chewing boolean not null,
  sleep_duration real not null,
  water_intake real not null,
  stress_frequency text not null,
  jaw_pain_frequency text not null,
  exercise_consistency text not null,
  smart_analysis text not null,
  created_at timestamp with time zone default timezone('utc'::text, now()) not null
);
alter table public.assessment_records enable row level security;
create policy "Users can manage own assessment records" on public.assessment_records for all using (auth.uid() = user_id);
