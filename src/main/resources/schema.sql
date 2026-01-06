
CREATE TABLE public.classrooms (
  id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  name text NOT NULL,
  capacity integer NOT NULL,
  CONSTRAINT classrooms_pkey PRIMARY KEY (id)
);
CREATE TABLE public.courses (
  id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  name text NOT NULL,
  description text,
  lecturer_id uuid NOT NULL,
  CONSTRAINT courses_pkey PRIMARY KEY (id),
  CONSTRAINT courses_lecturer_id_fkey FOREIGN KEY (lecturer_id) REFERENCES public.users(id)
);
CREATE TABLE public.enrollments (
  id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  student_id uuid NOT NULL,
  course_id bigint NOT NULL,
  grade text,
  CONSTRAINT enrollments_pkey PRIMARY KEY (id),
  CONSTRAINT enrollments_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(id),
  CONSTRAINT enrollments_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(id)
);
CREATE TABLE public.roles (
  id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  name text NOT NULL UNIQUE,
  CONSTRAINT roles_pkey PRIMARY KEY (id)
);
CREATE TABLE public.schedules (
  id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  course_id bigint NOT NULL,
  classroom_id bigint NOT NULL,
  start_time timestamp with time zone NOT NULL,
  end_time timestamp with time zone NOT NULL,
  CONSTRAINT schedules_pkey PRIMARY KEY (id),
  CONSTRAINT schedules_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(id),
  CONSTRAINT schedules_classroom_id_fkey FOREIGN KEY (classroom_id) REFERENCES public.classrooms(id)
);
CREATE TABLE public.user_roles (
  user_id uuid NOT NULL,
  role_id bigint NOT NULL,
  CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
  CONSTRAINT user_roles_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id),
  CONSTRAINT user_roles_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.roles(id)
);
CREATE TABLE public.users (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  username text NOT NULL UNIQUE,
  name text NOT NULL,
  email text NOT NULL UNIQUE,
  type text NOT NULL CHECK (type = ANY (ARRAY['student'::text, 'lecturer'::text])),
  password text NOT NULL,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone DEFAULT now(),
  CONSTRAINT users_pkey PRIMARY KEY (id)
);