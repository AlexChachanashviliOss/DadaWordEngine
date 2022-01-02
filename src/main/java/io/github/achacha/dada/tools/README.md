What is this?
===
Internal tool for verifying data and removing duplicates.

---

Parameters for WordDataConsole
---
**What we want to do**: Verify data set called 'extended2018'

**Parameters**:

    -action verify -dataset extended2018

---

**What we want to do**: Read data and remove duplicate entries and save new data file to /tmp

**Parameters**:

    -action dedup -dataset new2019 -outpath /tmp

_NOTE: Only files that have duplicates are saved_

---

**What we want to do**: Transform random words using specific phonemix transformer

**Parameters**:

    -action randomXform -dataset extended2018 -phonemix PhonemixCompacting
    -action randomXform -dataset extended2018 -phonemix PhonemixAggressive
    -action randomXform -dataset extended2018 -phonemix PhonemixEnhanced

---
