# ExtractionShooter 專案開發路線圖 (Roadmap)

本文件記錄《ExtractionShooter》後續開發之四大方向與推進進度。

---

## 🎯 核心方向總覽

### 方向 1：生命狀態 HUD 與肢體健康系統完善（第一步已完成 ✅）
*目標：讓玩家能即時掌握自身身體狀態，把部位傷害、負面狀態與醫療體系真正跑通。*
- [x] **規劃與架構分析**
- [x] **1.1 生命數據網路同步 (`SyncHealthPayload`)**
  - 新增 [`SyncHealthPayload`](file:///home/user/IdeaProjects/ExtractionShooter/src/main/kotlin/com/gmail/vincent031525/extractionshooter/network/payload/SyncHealthPayload.kt) 與 [`PlayerHealth.STREAM_CODEC`](file:///home/user/IdeaProjects/ExtractionShooter/src/main/kotlin/com/gmail/vincent031525/extractionshooter/dataattachment/PlayerHealth.kt)。
  - 建立 [`HealthUtils.syncHealth`](file:///home/user/IdeaProjects/ExtractionShooter/src/main/kotlin/com/gmail/vincent031525/extractionshooter/util/HealthUtils.kt)，在受傷 ([`DamageHandler`](file:///home/user/IdeaProjects/ExtractionShooter/src/main/kotlin/com/gmail/vincent031525/extractionshooter/event/DamageHandler.kt))、治療 ([`HealItem`](file:///home/user/IdeaProjects/ExtractionShooter/src/main/kotlin/com/gmail/vincent031525/extractionshooter/item/HealItem.kt) / [`SurgeryKitItem`](file:///home/user/IdeaProjects/ExtractionShooter/src/main/kotlin/com/gmail/vincent031525/extractionshooter/item/SurgeryKitItem.kt))、登入/重生/切換維度 ([`PlayerEventHandler`](file:///home/user/IdeaProjects/ExtractionShooter/src/main/kotlin/com/gmail/vincent031525/extractionshooter/event/PlayerEventHandler.kt)) 時同步給客戶端。
- [x] **1.2 仿 Tarkov 左上角肢體紙娃娃 HUD ([`HealthHudOverlay`](file:///home/user/IdeaProjects/ExtractionShooter/src/main/kotlin/com/gmail/vincent031525/extractionshooter/client/gui/HealthHudOverlay.kt))**
  - 於遊戲左上角繪製人體紙娃娃剪影、總血量 (Total HP 185) 與頭部 (HD 35)、胸部 (TH 85)、腿部 (LG 65) 之動態血量條與健康顏色（綠 -> 黃 -> 橙 -> 紅 -> 黑毀損）。
  - 即時顯示負面狀態徽章：`[BLEED]` / `[BLEED II]`（流血）、`[FRACTURE]`（骨折）、`[PK]`（止痛）。
  - 透過 [`HudEventHandler`](file:///home/user/IdeaProjects/ExtractionShooter/src/main/kotlin/com/gmail/vincent031525/extractionshooter/client/event/HudEventHandler.kt) 的 `RenderGuiEvent.Post` 於遊戲中自動渲染。
- [x] **1.3 背包介面 ([`GridInventoryScreen`](file:///home/user/IdeaProjects/ExtractionShooter/src/main/kotlin/com/gmail/vincent031525/extractionshooter/client/screen/GridInventoryScreen.kt)) 生命狀態整合**
  - 打開網格背包時，左側自動渲染當前身體各部位狀態卡片，方便一邊整理藥品一邊檢視生命狀態。
- [ ] **1.4 醫療品優先級與治療反饋優化**
  - 手持醫療包使用時，依受傷嚴重度治療，並提供完善使用音效與數值回饋。

---

### 方向 2：射擊手感與戰鬥表現升級 (Gunplay & Combat Feel)
*目標：讓槍戰具備現代射擊遊戲的精準度、沉浸感與回饋感。*
- [ ] **2.1 右鍵瞄準機制 (ADS - Aim Down Sights)**
  - 按住右鍵平滑調整 FOV 與相機視角/武器持槍位置。
  - 區分腰射 (Hipfire) 與開鏡 (ADS) 的精準度散布 (Spread) 與後座力倍率。
- [ ] **2.2 槍火視覺與專屬音效 (Audio & Visuals)**
  - 加入槍口閃光 (Muzzle Flash)、子彈曳光彈道 (Tracers) 與命中粒子 (Impact Particles)。
  - 實裝自訂開火音效、換彈卡榫音效與退彈殼聲，替換預設的 `GENERIC_EXPLODE`。
- [ ] **2.3 第二把武器：手槍 (Pistol) 實裝**
  - 新增一把副手手槍（如 Glock 17 或 M9），支援 `pistol` 裝備槽與對應彈藥/彈匣。

---

### 方向 3：背包操作手感優化與搜刮容器 (Inventory UX & Looting)
*目標：提升網格背包的互動流暢度，並支援世界容器搜刮。*
- [ ] **3.1 快捷操作與互動便利性**
  - **快速丟棄**：支援滑鼠懸浮按 `Q` / `Delete` 快速丟棄，或手持物品點擊介面外丟棄至世界中。
  - **快捷換裝 (Shift+Click)**：點擊背包/背心內的裝備或武器時，自動穿戴至對應裝備槽。
- [ ] **3.2 世界搜刮雙面板介面 (Looting Container Screen)**
  - 支援點擊箱子/死者掉落物時開啟搜刮介面（左側為自身背包、右側為容器網格）。

---

### 方向 4：撤離循環與戰局機制 (Extraction & Game Loop)
*目標：讓遊戲真正具備「撤離射擊 (Extraction Shooter)」的完整遊戲閉環。*
- [ ] **4.1 撤離點系統 (Extraction Zone / Exfil)**
  - 撤離點區域判定（座標範圍），進入後觸發撤離倒數（如 7 秒綠色計時條），計時結束安全返回或結算。
- [ ] **4.2 死亡掉落規則 (Death Rules)**
  - 調整死亡規則：僅保留 `secure_container`（安全箱/保險箱），其餘裝備（武器、護甲、背包、胸掛）於死亡地點生成掉落包。
- [ ] **4.3 戰局時間與倒數 (Raid Timer HUD)**
  - 螢幕右上角顯示戰局剩餘時間（如 25:00），逾時未撤離判定為 MIA (Missing in Action)。
