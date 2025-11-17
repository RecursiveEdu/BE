ALTER TABLE USERS ADD public_id VARCHAR(50) NOT NULL UNIQUE DEFAULT gen_random_uuid();
CREATE INDEX idx_users_public_id ON USERS(public_id);

-- exams
DROP TABLE IF EXISTS exams;
CREATE TABLE exams (
  id BIGSERIAL PRIMARY KEY,
  public_id VARCHAR(50) NOT NULL UNIQUE DEFAULT gen_random_uuid(),
  title VARCHAR(250) NOT NULL,
  description TEXT,
  duration_minutes INT NOT NULL,
  total_marks INT NOT NULL,
  start_time TIMESTAMP WITHOUT TIME ZONE,
  end_time TIMESTAMP WITHOUT TIME ZONE,
  created_by BIGINT REFERENCES users(user_id),
  is_published BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_exams_created_by ON exams(created_by);
CREATE INDEX idx_exams_public_id ON exams(public_id);
CREATE INDEX idx_exams_published ON exams(is_published);

-- exam_rules (dynamic selection rules)
DROP TABLE IF EXISTS exam_rules;
CREATE TABLE exam_rules (
  id BIGSERIAL PRIMARY KEY,
  exam_id BIGINT REFERENCES exams(id) ON DELETE CASCADE,
  tag_name VARCHAR(120),
  difficulty VARCHAR(50),
  question_count INT DEFAULT 1
);

CREATE INDEX idx_exam_rules_exam ON exam_rules(exam_id);

-- =====================================================
-- TAGS
-- =====================================================
DROP TABLE IF EXISTS tag;
CREATE TABLE tag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- questions
DROP TABLE IF EXISTS questions;
CREATE TABLE questions (
  id BIGSERIAL PRIMARY KEY,
  public_id VARCHAR(50) NOT NULL UNIQUE DEFAULT gen_random_uuid(),
  question_text TEXT NOT NULL,
  question_type VARCHAR(30) NOT NULL, -- 'MCQ','TRUE_FALSE','SHORT_ANSWER'
  difficulty VARCHAR(30),
  marks INT NOT NULL DEFAULT 1,
  created_by BIGINT REFERENCES users(user_id),
  tags jsonb,                    -- optional: list/attributes for quick filter
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITHOUT TIME ZONE
);

-- index JSONB tags if used
CREATE INDEX idx_questions_tags_gin ON questions USING gin (tags);

CREATE INDEX idx_questions_difficulty ON questions(difficulty);
CREATE INDEX idx_questions_created_by ON questions(created_by);

-- question_tags (alternative normalized tags)
-- Junction table for question <-> tag (Many-to-Many)
DROP TABLE IF EXISTS question_tag;
CREATE TABLE question_tag (
    question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tag(id) ON DELETE CASCADE,
    PRIMARY KEY (question_id, tag_id)
);
CREATE INDEX idx_tag_name ON tag(name);
CREATE INDEX idx_question_tag_tag_id ON question_tag(tag_id);
CREATE INDEX idx_question_tag_question_id ON question_tag(question_id);

-- options (for MCQ)
DROP TABLE IF EXISTS options;
CREATE TABLE options (
  id BIGSERIAL PRIMARY KEY,
  public_id VARCHAR(50) NOT NULL UNIQUE DEFAULT gen_random_uuid(),
  question_id BIGINT REFERENCES questions(id) ON DELETE CASCADE,
  option_text TEXT NOT NULL,
  is_correct BOOLEAN DEFAULT FALSE
);
CREATE INDEX idx_options_question_id ON options(question_id);

-- enrollments
DROP TABLE IF EXISTS enrollments;
CREATE TABLE enrollments (
  id BIGSERIAL PRIMARY KEY,
  public_id VARCHAR(50) NOT NULL UNIQUE DEFAULT gen_random_uuid(),
  user_id BIGINT REFERENCES users(user_id),
  exam_id BIGINT REFERENCES exams(id),
  enrolled_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  CONSTRAINT unique_user_exam UNIQUE (user_id, exam_id)
);
CREATE INDEX idx_enrollments_user_exam ON enrollments(user_id, exam_id);
CREATE INDEX idx_exam_enrollments_user_id ON enrollments(user_id);
CREATE INDEX idx_exam_enrollments_exam_id ON enrollments(exam_id);

-- exam_attempts
DROP TABLE IF EXISTS exam_attempts;
CREATE TABLE exam_attempts (
  id BIGSERIAL PRIMARY KEY,
  public_id VARCHAR(50) NOT NULL UNIQUE DEFAULT gen_random_uuid(),
  enrollment_id BIGINT REFERENCES enrollments(id) ON DELETE CASCADE,
  started_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  submitted_at TIMESTAMP WITHOUT TIME ZONE,
  total_score INT DEFAULT 0,
  is_completed BOOLEAN DEFAULT FALSE,
  status VARCHAR(30) NOT NULL,
  version BIGINT DEFAULT 0, -- optimistic lock column
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITHOUT TIME ZONE
);
CREATE INDEX idx_attempts_enrollment_id ON exam_attempts(enrollment_id);

-- attempt_questions (stores snapshot of dynamic selection)
DROP TABLE IF EXISTS attempt_questions;
CREATE TABLE attempt_questions (
  id BIGSERIAL PRIMARY KEY,
  attempt_id BIGINT REFERENCES exam_attempts(id) ON DELETE CASCADE,
  question_id BIGINT REFERENCES questions(id),
  marks_assigned INT DEFAULT 1,
  order_no INT DEFAULT 0
);
CREATE INDEX idx_attempt_questions_attempt_id ON attempt_questions(attempt_id);

-- attempt_answers (user submitted answers)
DROP TABLE IF EXISTS attempt_answers;
CREATE TABLE attempt_answers (
  id BIGSERIAL PRIMARY KEY,
  attempt_id BIGINT REFERENCES exam_attempts(id) ON DELETE CASCADE,
  question_id BIGINT REFERENCES questions(id),
  selected_option_id BIGINT REFERENCES options(id),
  answer_payload TEXT, -- store short-answer or structured answer; requires PG jsonb
  is_correct BOOLEAN,
  score_earned INT DEFAULT 0
);
CREATE INDEX idx_attempt_answers_attempt_id ON attempt_answers(attempt_id);
