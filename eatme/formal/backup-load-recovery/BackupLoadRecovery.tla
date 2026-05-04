---- MODULE BackupLoadRecovery ----
EXTENDS Naturals, Sequences, FiniteSets

CONSTANTS Backups, BackupOrder, ReadableBackups, UnsafeBackups, MAIN, NONE

\* BackupOrder is newest first. Each backup appears exactly once.
ASSUME BackupOrderCoversBackups ==
  /\ Len(BackupOrder) = Cardinality(Backups)
  /\ \A i \in 1..Len(BackupOrder) : BackupOrder[i] \in Backups
  /\ \A b \in Backups : \E i \in 1..Len(BackupOrder) : BackupOrder[i] = b
  /\ \A i, j \in 1..Len(BackupOrder) :
      BackupOrder[i] = BackupOrder[j] => i = j

ASSUME ReadableBackupsAreBackups ==
  ReadableBackups \subseteq Backups

ASSUME UnsafeBackupsAreBackups ==
  UnsafeBackups \subseteq Backups

VARIABLES pc, attempt, unloadable, candidate, outcome, currentProject

vars == <<pc, attempt, unloadable, candidate, outcome, currentProject>>

Outcomes == {"Undecided", "LoadedBackup", "NewProject"}

OrderIndex(b) ==
  CHOOSE i \in 1..Len(BackupOrder) : BackupOrder[i] = b

AvailableBackups ==
  {b \in Backups : (b \notin unloadable) /\ (b \notin UnsafeBackups)}

NextBackup ==
  IF AvailableBackups = {}
  THEN NONE
  ELSE CHOOSE b \in AvailableBackups :
    \A other \in AvailableBackups : OrderIndex(b) <= OrderIndex(other)

Init ==
  /\ pc = "LoadingMain"
  /\ attempt = MAIN
  /\ unloadable = {}
  /\ candidate = NONE
  /\ outcome = "Undecided"
  /\ currentProject = "OriginalValid"

TypeOK ==
  /\ pc \in {"LoadingMain", "SelectingBackup", "PromptBackup", "LoadingBackup", "Final"}
  /\ attempt \in Backups \cup {MAIN, NONE}
  /\ unloadable \subseteq Backups \cup {MAIN}
  /\ candidate \in Backups \cup {NONE}
  /\ outcome \in Outcomes
  /\ currentProject \in Backups \cup {"OriginalValid", "NewProject"}

MainLoadFails ==
  /\ pc = "LoadingMain"
  /\ attempt = MAIN
  /\ pc' = "SelectingBackup"
  /\ attempt' = NONE
  /\ unloadable' = unloadable \cup {MAIN}
  /\ candidate' = NONE
  /\ UNCHANGED <<outcome, currentProject>>

OfferReadableBackup ==
  /\ pc = "SelectingBackup"
  /\ NextBackup # NONE
  /\ NextBackup \in ReadableBackups
  /\ pc' = "PromptBackup"
  /\ candidate' = NextBackup
  /\ UNCHANGED <<attempt, unloadable, outcome, currentProject>>

SkipUnreadableBackup ==
  /\ pc = "SelectingBackup"
  /\ NextBackup # NONE
  /\ NextBackup \notin ReadableBackups
  /\ pc' = "SelectingBackup"
  /\ attempt' = NONE
  /\ unloadable' = unloadable \cup {NextBackup}
  /\ candidate' = NONE
  /\ UNCHANGED <<outcome, currentProject>>

NoBackupRemaining ==
  /\ pc = "SelectingBackup"
  /\ NextBackup = NONE
  /\ pc' = "Final"
  /\ attempt' = NONE
  /\ candidate' = NONE
  /\ outcome' = "NewProject"
  /\ currentProject' = "NewProject"
  /\ UNCHANGED unloadable

AcceptBackup ==
  /\ pc = "PromptBackup"
  /\ candidate \in ReadableBackups
  /\ pc' = "LoadingBackup"
  /\ attempt' = candidate
  /\ candidate' = NONE
  /\ UNCHANGED <<unloadable, outcome, currentProject>>

DeclineBackup ==
  /\ pc = "PromptBackup"
  /\ pc' = "Final"
  /\ attempt' = NONE
  /\ candidate' = NONE
  /\ outcome' = "NewProject"
  /\ currentProject' = "NewProject"
  /\ UNCHANGED unloadable

BackupLoadSucceeds ==
  /\ pc = "LoadingBackup"
  /\ attempt \in ReadableBackups
  /\ pc' = "Final"
  /\ currentProject' = attempt
  /\ attempt' = NONE
  /\ candidate' = NONE
  /\ outcome' = "LoadedBackup"
  /\ UNCHANGED unloadable

Next ==
  \/ MainLoadFails
  \/ OfferReadableBackup
  \/ SkipUnreadableBackup
  \/ NoBackupRemaining
  \/ AcceptBackup
  \/ DeclineBackup
  \/ BackupLoadSucceeds

Spec ==
  /\ Init
  /\ [][Next]_vars
  /\ WF_vars(Next)

CorruptPrimaryDoesNotReplaceCurrentBeforeFinal ==
  pc # "Final" => currentProject = "OriginalValid"

LoadedBackupWasReadable ==
  outcome = "LoadedBackup" => currentProject \in ReadableBackups

PromptedBackupsAreReadable ==
  pc = "PromptBackup" => candidate \in ReadableBackups

PromptedBackupsAreSafe ==
  pc = "PromptBackup" => candidate \notin UnsafeBackups

UnloadableBackupsSkipped ==
  outcome = "LoadedBackup" =>
    \A b \in Backups :
      OrderIndex(b) < OrderIndex(currentProject) => b \in unloadable \cup UnsafeBackups

FinalOutcomeExactlyOne ==
  pc = "Final" =>
    /\ outcome \in {"LoadedBackup", "NewProject"}
    /\ attempt = NONE
    /\ candidate = NONE

FinalProjectMatchesOutcome ==
  pc = "Final" =>
    \/ /\ outcome = "LoadedBackup"
       /\ currentProject \in ReadableBackups
       /\ currentProject \notin UnsafeBackups
    \/ /\ outcome = "NewProject"
       /\ currentProject = "NewProject"

NoStaleAsyncCompletion ==
  pc = "Final" =>
    /\ attempt = NONE
    /\ candidate = NONE

EventuallyFinal ==
  <> (pc = "Final")

====
