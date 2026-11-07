import type { Metadata } from "next";
import { Archivo } from "next/font/google";
import Link from "next/link";
import "./globals.css";
import { AuthProvider } from "@/components/AuthProvider";
import { Header } from "@/components/Header";

const archivo = Archivo({
  subsets: ["latin"],
  axes: ["wdth"],
  variable: "--font-archivo",
  display: "swap",
});

export const metadata: Metadata = {
  title: "Worker Hiring",
  description: "Shop owners post work. Local workers apply. Find helpers and jobs near you.",
};

// Apply the saved theme before paint so there's no flash.
const themeScript = `try{var t=localStorage.getItem("wh_theme");if(t==="light"||t==="dark")document.documentElement.dataset.theme=t}catch(e){}`;

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={archivo.variable} suppressHydrationWarning>
      <head>
        <script dangerouslySetInnerHTML={{ __html: themeScript }} />
      </head>
      <body className="min-h-screen flex flex-col">
        <AuthProvider>
          <Header />
          <main className="flex-1 w-full">{children}</main>
          <footer className="border-t border-line mt-16">
            <div className="mx-auto max-w-5xl px-4 py-6 text-sm text-muted flex flex-wrap gap-x-6 gap-y-2">
              <span>Worker Hiring</span>
              <Link href="/about" className="hover:text-text underline-offset-4 hover:underline">
                About
              </Link>
            </div>
          </footer>
        </AuthProvider>
      </body>
    </html>
  );
}
