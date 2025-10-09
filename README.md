<div align="center">

# [2025] 📰 Todayter  
『 ${\textsf{\color{blue}today + data + reporter}}$ - 오늘을 기록하고 공유하는 뉴스 플랫폼 』


Todayter는 관리자가 기사를 작성·승인하고, 사용자가 뉴스를 열람하고 소통할 수 있는 **양방향 뉴스 플랫폼**입니다. <br>
주요 뉴스 포털의 UI/UX 구성(헤드라인, 카테고리, 랭킹, 기사 카드형 UI 등)을 벤치마킹하여 뉴스 콘텐츠의 생산–관리–소비 전 과정을 재구성한 개인 프로젝트입니다.
<br><br>
${\textsf{\color{teal}해당\ 프로젝트는\ 기획부터\ 설계,\ 개발,\ 배포까지\ 단독으로\ 수행한\ 개인\ 프로젝트입니다.}}$

<img width="400" height="400" alt="todayter_캐릭터 (1)" src="https://github.com/user-attachments/assets/bebee899-beb6-4fe3-86ce-0fdfe2d5bd47" />


👩🏻‍💻 **기사 관리 시스템** : 기사 등록·수정·승인 절차를 통해 검증된 콘텐츠만 노출됩니다.
<br>

💬 **참여와 소통** : 사용자는 기자를 구독하고 응원할 수 있으며 댓글과 좋아요로 의견을 나눌 수 있습니다.
<br>

📊 **데이터 기반 뉴스** : 관리자 페이지를 통해 기사별 통계와 트렌드를 확인하고 더 나은 콘텐츠를 제작할 수 있습니다.
<br>

📌 **오늘의 하이라이트** : 바쁠 경우 PICK 기사를 통해 핵심 뉴스를 빠르게 확인할 수 있습니다.

</div>
<br>

## 목차
- [🎨 Tech Stack](#tech-stack)
- [📑 Technical Documentation](#tech)
- [🛫 Features](#features)
- [🌌 Environment Variable](#ghksrud)

<br>

<div id="tech-stack">

# 🎨 Tech Stack

✔ RESTful API를 통해 BE·FE를 분리 구성하였으며 프론트엔드는 다양한 AI 모델을 활용하여 개발되었습니다

| Type | Tech | Version | Link |
|------|------|----------|------|
| **IDE** | ![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-%23000000.svg?style=for-the-badge&logo=intellijidea&logoColor=white) | 2024.1 | |
| **Framework** | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white) | 3.3.0 | |
| **Language** | ![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) | JDK 17 | |
| **Frontend** | ![React](https://img.shields.io/badge/React-%2361DAFB.svg?style=for-the-badge&logo=react&logoColor=white) <br> ![Axios](https://img.shields.io/badge/Axios-%235A29E4.svg?style=for-the-badge&logo=axios&logoColor=white) <br> ![TailwindCSS](https://img.shields.io/badge/TailwindCSS-%2306B6D4.svg?style=for-the-badge&logo=tailwindcss&logoColor=white) | React 18.3.1 <br> Axios 1.10.0 <br> TailwindCSS 2.2.17 | |
| **Database** | ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-%234169E1.svg?style=for-the-badge&logo=postgresql&logoColor=white) <br> ![Redis](https://img.shields.io/badge/Redis-%23DC382D.svg?style=for-the-badge&logo=redis&logoColor=white) | PostgreSQL 15.12 <br> Redis 3.0.504 (Local) / 7.4.5 (Docker) | |
| **DevOps** | ![Docker](https://img.shields.io/badge/Docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white) <br> ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-%232088FF.svg?style=for-the-badge&logo=githubactions&logoColor=white) <br> ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazonaws&logoColor=white) | Docker 27.5.1 <br> GitHub Actions (CI/CD · ubuntu-24.04 Runner) <br> AWS (EC2, S3, VPC) | |
| **Infra** | ![Nginx](https://img.shields.io/badge/Nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white) <br> ![Certbot](https://img.shields.io/badge/Certbot-%23FFFFFF.svg?style=for-the-badge&logo=letsencrypt&logoColor=%23FDB813) | Nginx 1.29.1 <br> Certbot 2.9.0 (auto renewal) | |
| **Security** | ![Spring Security](https://img.shields.io/badge/Spring%20Security-%236DB33F.svg?style=for-the-badge&logo=springsecurity&logoColor=white) <br> ![OAuth 2.0](https://img.shields.io/badge/OAuth%202.0-%234E9A06.svg?style=for-the-badge&logo=openid&logoColor=white) | Google / Naver / Kakao OAuth 연동 | |
| **Tools** | ![GitHub](https://img.shields.io/badge/GitHub-%23121011.svg?style=for-the-badge&logo=github&logoColor=white) <br> ![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white) <br> ![dbdiagram.io](https://img.shields.io/badge/dbdiagram.io-%23007ACC.svg?style=for-the-badge&logo=diagramsdotnet&logoColor=white) <br> ![Cloudcraft](https://img.shields.io/badge/Cloudcraft-%2300AEEF.svg?style=for-the-badge&logo=cloudsmith&logoColor=white) |  | [🔗 Notion](https://www.notion.so/todayter-1ee84a288599805e8d07ef510d8f3646) |
| **Video** | <img align="left" alt="YouTube" width="48px" src="https://img.icons8.com/color/48/000000/youtube-play.png" /> | | [🔗 YouTube](https://youtu.be/Yu4CT89j82c) |

**💥 모든 기능은 유튜브를 통해 확인할 수 있습니다.**


<br>

<div id="tech">


# 📑 Technical Documentation

<details>
<summary>🏗 Architecture</summary>
<div markdown="1">
  
## 🏗

<img width="1931" height="1201" alt="Web App Reference Architecture (1)" src="https://github.com/user-attachments/assets/f58f09a9-f398-4712-9eaf-058c797d58f3" />


</div>
</details>

</div>
</details>

<details>
<summary>🧬 ERD DIAGRAM</summary>
<div markdown="1">
  

<img width="1542" height="1163" alt="erd" src="https://github.com/user-attachments/assets/30c457d8-32e6-4810-9375-0c4f53783d17" />


</div>
</details>


<details>
<summary> 🔨 API 명세서</summary>

[🔗 API 명세서 보기 (Notion)](https://www.notion.so/API-27e84a2885998038a925d55852ce99e7)

</div>
</details>



</div>
</details>


<details>
<summary>🌠 Commit Rule</summary>
<div markdown="1">
  
## 🌠 Commit Rule
- **[#이슈번호] '작업 타입' : '작업 내용'**
> ex)  
> [#36] ✨ feat : 회원가입 기능 추가
> - 구체적인 내용1
> - 구체적인 내용2
> - 구체적인 내용3
> - 구체적인 내용이 있을 경우을 아래에 작성
> - 여러 줄의 메시지를 작성할 땐 "-"로 구분

<br>

| 작업 타입 | 작업내용 |
| --- | --- |
| ✨ feat | 새로운 기능을 추가 |
| 🎉 add | 없던 파일을 생성함, 초기 세팅 |
| 🐛 bugfix | 버그 수정 |
| ♻️ refactor | 코드 리팩토링 |
| 🩹 fix | 코드 수정 |
| 🚚 move | 파일 옮김/정리 |
| 🔥 del | 기능/파일을 삭제 |
| 🍻 test | 테스트 코드를 작성 |
| 🎨 readme | readme 수정 |
| 🙈 gitfix | gitignore 수정 |
| 🔨script | package.json 변경(npm 설치 등) |


</div>
</details>

<br>

# 🛫 Features

<details>
<summary> 👧🏻 사용자 기능 </summary>

- **회원가입 및 인증**
  - 이메일 인증 기반 회원가입
  - 일반 사용자 / 관리자 역할 구분
  - 카카오, 구글, 네이버 소셜 로그인
- **로그인 및 계정 관리**
  - 일반 로그인 및 자동 로그인 유지
  - 로그아웃 및 세션 만료 처리
  - 닉네임 / 비밀번호 / 프로필 이미지 수정
  - 비밀번호 변경은 30일 내 최대 3회 제한
  - 구독한 기자 목록 확인 및 관리
  - 회원 탈퇴
- **기사 열람 및 탐색**
  - 실시간 Top 5 랭킹 기사
  - PICK 기사 / 최신 기사 / 많이 본 기사 / 인기 기사
  - 지역별(익산·전주·군산) 및 분야별(정치·문화·교육 등) 기사 조회
  - 키워드 기반 기사 검색 및 인기 검색어 확인
- **소통 및 반응**
  - 기사 좋아요 / 좋아요 취소
  - 댓글 및 대댓글 작성, 수정, 삭제
  - 댓글 / 대댓글 좋아요 및 취소
- **기자와의 상호작용**
  - 기자 구독 및 응원 기능
  - 구독 시 성별 / 연령 입력 → 기자 통계 반영

</details>

<details>
<summary>👩🏻‍🔧 관리자 기능 </summary>

- **대시보드**
  - 전체 회원, 댓글, 기사(승인/미승인) 통계 시각화
  - 실시간 기사 승인 상태 및 신규 활동 모니터링
- **회원 관리**
  - 전체 회원 조회 및 검색
  - 일반 회원을 관리자 계정으로 승격
- **기사 관리**
  - 기사 등록: 1차 분류(지역/분야) → 2차 분류 → 제목/부제/본문(텍스트·이미지)
  - 기사 승인 / 미승인 설정 및 수정 / 삭제
  - PICK 기사 지정
  - 기사 검색 및 정렬 기능
- **댓글 관리**
  - 전체 댓글 조회 및 삭제
  - 비속어 자동 필터링 시스템 적용
- **기자 프로필 관리**
  - 프로필 이미지 등록 / 수정
  - 월별 기사 작성 통계 조회
  - 구독자 통계(연령 / 성별 비율) 분석

</details>


<br>

<div id="ghksrud">
  
# 🌌 Environment Variable
```env
JWT_SECRET_KEY={jwt_secret_key}
ACCESS_TOKEN_EXPIRATION=3600000
REFRESH_TOKEN_EXPIRATION=1209600000
ADMIN_TOKEN={admin_token}
AWS_ACCESS_KEY={aws_access_key}
AWS_SECRET_KEY={aws_secret_key}
AWS_REGION=ap-northeast-2
AWS_BUCKET_NAME=todayter
DB_HOST=jdbc:postgresql://postgres:5432/todayter
USERNAME=postgres
PASSWORD={db_password}
REDIS_HOST=todayter-redis
REDIS_PORT=6379
REDIS_PASSWORD={redis_password}
SOCIAL_GOOGLE_CLIENT_ID={google_client_id}
SOCIAL_GOOGLE_CLIENT_SECRET={google_client_secret}
SOCIAL_GOOGLE_REDIRECT_URI=https://todayter.store/login/oauth2/code/google
SOCIAL_NAVER_CLIENT_ID={naver_client_id}
SOCIAL_NAVER_CLIENT_SECRET={naver_client_secret}
SOCIAL_NAVER_REDIRECT_URI=https://todayter.store/login/oauth2/code/naver
SOCIAL_KAKAO_CLIENT_ID={kakao_client_id}
SOCIAL_KAKAO_CLIENT_SECRET={kakao_client_secret}
SOCIAL_KAKAO_REDIRECT_URI=https://todayter.store/login/oauth2/code/kakao
EMAIL=todayterofficial@gmail.com
APP_PASSWORD={app_password}
APP_OAUTH2_REDIRECT_URL=https://todayter.store/oauth2/redirect
BAD_WORDS={}
BAD_WORD_REGEX=()
DEFAULT_IMAGE_URL={default_image_url}
SERVER_FORWARD_HEADERS_STRATEGY=framework
```
