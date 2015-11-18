package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import commun.Match;

public class LNH {
	private Map<Integer, Match> matchs;
	private Map<Integer, Thread> threadGestionnairesDePari;
	private Map<Integer, MatchBetManager> gestionnairesDePari;
	Thread chronoUpdater;
	Thread matchUpdater;

	// Lock pour la liste de match
	private final ReadWriteLock readWriteLockMatch = new ReentrantReadWriteLock();
	private final Lock readLockMatch = readWriteLockMatch.readLock();
	private final Lock writeLockMatch = readWriteLockMatch.writeLock();

	public LNH() {
		matchs = new HashMap<Integer, Match>();
		threadGestionnairesDePari = new HashMap<Integer, Thread>();
		gestionnairesDePari = new HashMap<Integer, MatchBetManager>();
		chronoUpdater = new Thread(new ChronoUpdater(this));
		matchUpdater = new Thread(new MatchUpdater(this));
		chronoUpdater.start();
		matchUpdater.start();
	}

	public void startMatch(Match m) {
		if (matchs.values().size() < 10) {
			writeLockMatch.lock();
			try {
				MatchBetManager mbm = new MatchBetManager(m);
				Thread tmbm = new Thread(mbm);
				tmbm.start();

				matchs.put(m.getId(), m);
				gestionnairesDePari.put(m.getId(), mbm);
				threadGestionnairesDePari.put(m.getId(), tmbm);
			} finally {
				writeLockMatch.unlock();
			}
		}
	}

	public Map<Integer, String> getMatchList() {
		readLockMatch.lock();
		try {
			Map<Integer, String> listeDesMatchs = new HashMap<Integer, String>();
			for (Match m : matchs.values())
				listeDesMatchs.put(m.getId(), m.toString());
			return listeDesMatchs;
		} finally {
			readLockMatch.unlock();
		}
	}

	public Match getMatchDetails(Integer matchId) {
		return matchs.get(matchId);
	}

	public MatchBetManager getGestionnaireDePari(Integer matchId) {
		return gestionnairesDePari.get(matchId);
	}

	public void endMatch(Integer matchId) {
		writeLockMatch.lock();
		try {
			// Obtenir les objets
			Match m = matchs.get(matchId);
			MatchBetManager mbm = gestionnairesDePari.get(matchId);
			Thread tmbm = threadGestionnairesDePari.get(matchId);
			// Retirer les objets des collections
			gestionnairesDePari.remove(matchId);
			threadGestionnairesDePari.remove(matchId);
			// Arreter le thread des paris
			tmbm.interrupt();
			// Calculer et envoyer les messages de paris
			mbm.finalizeGame();
		} finally {
			writeLockMatch.unlock();
		}

	}

	public class ChronoUpdater implements Runnable {
		LNH lnh;

		public ChronoUpdater(LNH lnh) {
			this.lnh = lnh;
		}

		public void run() {
			List<Match> matchEndings = new ArrayList<Match>();
			while (true) {
				writeLockMatch.lock();
				try {
					for (Match m : lnh.matchs.values()) {
						if (m.getPeriode() == 4)
							continue;
						m.setChronometre(m.getChronometre() - 11);// 0.5);
						// Gestion des penalités
						if (m.getPenalitesA().size() != 0)
							for (int i = m.getPenalitesA().size() - 1; i >= 0; i--)
								if (m.getPenalitesA().get(i).chronometreLiberation >= m.getChronometre())
									m.getPenalitesA().remove(i);
						if (m.getPenalitesB().size() != 0)
							for (int i = m.getPenalitesB().size() - 1; i >= 0; i--)
								if (m.getPenalitesB().get(i).chronometreLiberation >= m.getChronometre())
									m.getPenalitesB().remove(i);
						// Gestion des périodes
						if (m.getChronometre() <= 0) {
							m.setChronometre(20.0);
							m.setPeriode(m.getPeriode() + 1);
							if (m.getPeriode() == 4)
								matchEndings.add(m);
						}
					}
					// End matches
					for (Match m : matchEndings)
						lnh.endMatch(m.getId());
					matchEndings.clear();
				} finally {
					writeLockMatch.unlock();
				}
				// Sleep
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	public class MatchUpdater implements Runnable {
		LNH lnh;

		public MatchUpdater(LNH lnh) {
			this.lnh = lnh;
		}

		public void run() {
			while (true) {
				writeLockMatch.lock();

				try {
					for (Match m : lnh.matchs.values()) {
						if (m.getPeriode() == 4)
							continue;
						// Generation aleatoire d'evenemnts
						Integer event = randomRange(0, 40);
						if (event == 0) // Equipe A score
							m.scoreEquipeA("Dat name");
						else if (event == 1) // Equipe B score
							m.scoreEquipeB("Dat name");
						else if (event == 2 || event == 3)// 2 minutes equipe A
							m.penaliteEquipeA("Dat name", 2);
						else if (event == 4 || event == 5)// 2 minutes equipe B
							m.penaliteEquipeB("Dat name", 2);
						else if (event == 6 || event == 7)// 5 minutes equipe A
							m.penaliteEquipeA("Dat name", 5);
						else if (event == 8 || event == 9)// 5 minutes equipe B
							m.penaliteEquipeB("Dat name", 5);
					}
				} finally {
					writeLockMatch.unlock();
				}
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					return;
				}
			}
		}

		private Integer randomRange(Integer min, Integer max) {
			return min + (int) (Math.random() * ((max - min) + 1));
		}
	}
}
