# Visual Reference Guide

## App Flow Diagram

```
┌─────────────────────────────────────────┐
│        Launch App                       │
└────────────────┬────────────────────────┘
                 │
                 ▼
        ┌─────────────────┐
        │ First Launch?   │
        └────┬────────┬───┘
          YES│        │NO
             │        │
    ┌────────▼─┐   ┌──▼────────────┐
    │Onboarding│   │Main Dashboard │
    │  5 Pages │   │  Grid Cards   │
    └────┬─────┘   └──┬───────────┬┘
         │             │           │
         └─────────────┼───────────┴──────┐
                      │                   │
                    ┌─▼──────────────────▼──┐
                    │  Feature Selection    │
                    └─┬──┬──┬──┬──┬────────┘
                      │  │  │  │  │
         ┌────────────┤  │  │  │  │
         │            │  │  │  │  │
   ┌─────▼──────┬──────▼──┬──▼──┬───▼─────┬──────────┐
   │Text        │Image    │Sent  │Objects  │Video    │
   │Generation  │Generat  │iment │Detectio │Analysis │
   │            │         │      │n        │         │
   └────────────┴─────────┴──────┴────────┴─────────┘
```

## Onboarding Screens

### Screen 1: Welcome
```
┌──────────────────────────────┐
│                              │
│    ┌──────────────────┐      │
│    │   Welcome Icon  │      │
│    └──────────────────┘      │
│                              │
│  Welcome to CreativeAI       │
│                              │
│  Create stunning visual      │
│  content with AI-powered     │
│  tools                       │
│                              │
│  ● ○ ○ ○ ○                 │
│                              │
│        [Skip]        [Next]  │
└──────────────────────────────┘
```

### Screen 2-4: Features
```
Similar layout with different:
- Icons (Image, Video, Analyze)
- Titles (Generate, Create, Understand)
- Descriptions (feature-specific)
```

### Screen 5: Ready
```
┌──────────────────────────────┐
│                              │
│    ┌──────────────────┐      │
│    │   Rocket Icon   │      │
│    └──────────────────┘      │
│                              │
│  Ready to Create?            │
│                              │
│  Let's start building        │
│  amazing content together!   │
│                              │
│  ● ● ● ● ●                 │
│                              │
│           [Get Started]      │
└──────────────────────────────┘
```

## Main Dashboard

```
┌──────────────────────────────────────┐
│  ╔══════════════════════════════════╗│
│  ║  CreativeAI                      ║│
│  ║  Create stunning content with AI ║│
│  ╚══════════════════════════════════╝│
├──────────────────────────────────────┤
│                                      │
│  ┌──────────────┐  ┌──────────────┐ │
│  │              │  │              │ │
│  │  📝 Text     │  │  🖼️ Image    │ │
│  │              │  │              │ │
│  │ Generate     │  │ Generate     │ │
│  │ [Open]       │  │ [Open]       │ │
│  └──────────────┘  └──────────────┘ │
│                                      │
│  ┌──────────────┐  ┌──────────────┐ │
│  │              │  │              │ │
│  │  💭 Sentiment│  │  🎯 Objects  │ │
│  │              │  │              │ │
│  │ Analyze      │  │ Detect       │ │
│  │ [Open]       │  │ [Open]       │ │
│  └──────────────┘  └──────────────┘ │
│                                      │
│  ┌──────────────┐                    │
│  │              │                    │
│  │  🎬 Video    │                    │
│  │              │                    │
│  │ Analyze      │                    │
│  │ [Open]       │                    │
│  └──────────────┘                    │
└──────────────────────────────────────┘
```

## Color Palette

```
Primary Colors:
┌─────────────────────────────────┐
│ Indigo      #6366F1             │
│ █████████████████████████████   │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ Blue        #3B82F6             │
│ █████████████████████████████   │
└─────────────────────────────────┘

Neutral Colors:
┌─────────────────────────────────┐
│ Dark Text   #1F2937             │
│ █████████████████████████████   │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ Light Text  #6B7280             │
│ █████████████████████████████   │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ Background  #F9FAFB             │
│ █████████████████████████████   │
└─────────────────────────────────┘
```

## Feature Card Anatomy

```
┌─────────────────────────┐
│  ┌─────────────────┐    │
│  │   48x48 Icon   │    │ ← Feature icon
│  └─────────────────┘    │
│                         │
│  Text Generation        │ ← Bold title
│                         │
│  Generate text content  │ ← Description
│                         │
│  ┌────────────────────┐ │
│  │      [Open]        │ │ ← Action button
│  └────────────────────┘ │
│                         │
└─────────────────────────┘
```

## Button Styles

### Primary Button
```
┌────────────────────┐
│    [Open] [Next]   │ ← Bold text
│  Background: Indigo│
│  Text: White       │
│  Height: 40-48dp   │
│  Border Radius: 8dp│
└────────────────────┘
```

### Secondary Button (Skip)
```
┌────────────────────┐
│      [Skip]        │ ← Text button
│  Background: Clear │
│  Text: Indigo      │
│  Subtle styling    │
└────────────────────┘
```

## Icon Set

```
Text Icon          Image Icon         Sentiment Icon
   │││││              ▄▄▄                ◯◯◯
   │││││              █  █               ◯◯◯
   │││││              ▀▀▀                ◯◯◯
                                           ∨

Objects Icon       Video Icon         Voice Icon
┌─┬─┐              ▶ ▬▬▬              ♪ ♪
├─┼─┤              ▬▬▬▬               ♪ ♪ ♪
└─┴─┘              ▬▬▬
```

## Onboarding Illustrations

```
Welcome              Image              Video
    ◯                  □                  ▶
   /▲\                ◯□◯                ▬▬▬
   ▀ ▀                                    ▬▬▬

Analyze              Rocket
  ▯▯                    ▲
 ▯ ▯ ▯                 ╱ ╲
 ▯ ▯ ▯                ╱   ╲
```

## Layout Specifications

### Spacing
```
Padding: 16dp (default), 24dp (header), 12dp (cards)
Margin between cards: 12dp
Column gap: 12dp
Line spacing: 1.5x for body text
```

### Typography
```
Headers: 32sp, Bold
Titles: 16sp, Bold
Body: 16sp, Regular
Descriptions: 14sp, Regular, Secondary color
Labels: 12sp, Regular, Secondary color
Buttons: 14-16sp, Bold
```

### Dimensions
```
Feature Icons: 48x48dp
Onboarding Icons: 280x280dp
Button Height: 40-48dp
Button Width: match_parent (cards), 120dp (dialogs)
Card Elevation: 4dp
```

## User Journey

```
First Time User:
1. Launch → Onboarding
2. Page 1-4: Learn features
3. Page 5: Ready to start
4. Click "Get Started" → Main Dashboard
5. Dashboard loads
6. User selects feature
7. Feature activity opens
8. User inputs data
9. Backend processes
10. Results displayed

Returning User:
1. Launch → Main Dashboard (skip onboarding)
2. Select feature
3. Use as normal
```

## Error Handling Flow

```
User Input
    ↓
Validation Check
    ├─ Invalid → Show Error Message
    │               ↓
    │          User Retries
    │
    └─ Valid → Send to Backend
              ↓
        Processing...
              ↓
        Success → Display Results
        OR
        Error → Show Error + Retry Button
```

## Performance Indicators

```
Short Operations (1-5s):
  Sentiment Analysis
  Quick text processing
  Status: ⚡ Fast

Medium Operations (5-30s):
  Image Generation
  Object Detection
  Video Analysis
  Status: ⏳ Wait...

Long Operations (30-90s):
  Video Generation
  Status: ⏳ Please wait...
```

---

This visual reference helps understand the app's design and user flow!
