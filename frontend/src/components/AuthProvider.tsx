"use client";

import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { api, getToken, setToken } from "@/lib/api";
import type { AuthResponse, User } from "@/lib/types";

type Status = "loading" | "authed" | "anon";

interface AuthContextValue {
  status: Status;
  user: User | null;
  unread: number;
  login: (email: string, password: string) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => Promise<void>;
  setUser: (u: User) => void;
  refreshUnread: () => void;
  /** Clears local session without calling the server (e.g. after deleting the account). */
  clearSession: () => void;
}

export interface RegisterData {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
  password: string;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [status, setStatus] = useState<Status>("loading");
  const [user, setUserState] = useState<User | null>(null);
  const [unread, setUnread] = useState(0);

  const clearSession = useCallback(() => {
    setToken(null);
    setUserState(null);
    setUnread(0);
    setStatus("anon");
  }, []);

  useEffect(() => {
    if (!getToken()) {
      setStatus("anon");
      return;
    }
    api<User>("/api/auth/me")
      .then((u) => {
        setUserState(u);
        setStatus("authed");
      })
      .catch(() => clearSession());
  }, [clearSession]);

  const refreshUnread = useCallback(() => {
    if (!getToken()) return;
    api<{ unread: number }>("/api/notifications/unread-count")
      .then((r) => setUnread(r.unread))
      .catch(() => {});
  }, []);

  // Light polling keeps the bell count fresh without websockets.
  useEffect(() => {
    if (status !== "authed") return;
    refreshUnread();
    const id = window.setInterval(refreshUnread, 30000);
    return () => window.clearInterval(id);
  }, [status, refreshUnread]);

  const accept = (res: AuthResponse) => {
    setToken(res.token);
    setUserState(res.user);
    setStatus("authed");
  };

  const value: AuthContextValue = {
    status,
    user,
    unread,
    login: async (email, password) => accept(await api<AuthResponse>("/api/auth/login", "POST", { email, password })),
    register: async (data) => accept(await api<AuthResponse>("/api/auth/register", "POST", data)),
    logout: async () => {
      try {
        await api("/api/auth/logout", "POST");
      } finally {
        clearSession();
      }
    },
    setUser: setUserState,
    refreshUnread,
    clearSession,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}
