# WMmarket
## 목차
1. [DEV SERVER](#dev-server)
2. [POST MAN](#post-man)
3. [SWAGGER](#swagger)
4. [DOCUMENT](#document)
5. [CODE REVIEW](#code-review)

# DEV SERVER
## 서버링크 (https://wm-market.herokuapp.com/)
- paas 형태인 heroku 사용
- 연결이 24시간 인건 아니라서 최초 접속시 딜레이 생길 수 있음

- 배포는 수동으로 할수도 있음. (현재는 auto deploy)
``` git push heroku master ```

## [관리페이지](https://dashboard.heroku.com/apps/wm-market)
### [로그확인](https://dashboard.heroku.com/apps/wm-market/logs)

---

# POST MAN
- 로컬에서 테스트시에 간편함.
## [워크스페이스 링크](https://go.postman.co/workspace/Team-Workspace~5fdacef4-6989-4939-87ec-78d111ab3aec/collection/14335279-7d003794-b445-44e1-90cd-f6b9f6160b71)

### 주의사항
- request에 이미지 필요시 직접 넣어줘야함.
---
# SWAGGER
- [domain]/swagger-ui.html 로 접근
- 테스트 용도보단 문서 역할로 사용
- api call의 설명, 예제, 필수여부가 포함되어있음.

---

# DOCUMENT
## [구글 드라이브 링크](https://drive.google.com/drive/folders/19BDtsmnjCkTiiPqCFPvrxUHjke4UNqkP?usp=sharing)
- 인가된 계정만 read edit 가능

## [ERD CLOUD](https://www.erdcloud.com/d/PonvnYhMhuKBgyDHk)
- read는 누구나 가능
- edit은 팀만

# CODE REVIEW
[![CodeFactor](https://www.codefactor.io/repository/github/roongang/wmmarket/badge)](https://www.codefactor.io/repository/github/roongang/wmmarket)
