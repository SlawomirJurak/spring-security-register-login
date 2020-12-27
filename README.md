Aplikacja demonstrująca wykorzystanie Spring Security.

W aplikacji zostały zaimplementowane funkcje
 - /signup - rejestracja
   
Podczas rejestracji użytkownik wybiera czy chce być zwykłym użytkownikiem (rola user),
czy administratorem (rola admin). Po rejestracji jest wysyłany link aktywacyjny.
Dopóki użytkownik nie przejdzie po adres wysłany w wiadomości, konto
jest nieaktywne. Gdy przy rejestracji użytkownik wybrał typ konta Administrator,
to wysyłana jest też wiadomość do administratora serwisu w linkiem aktywacyjnym takiego konta.
Dopóki administrator serwisu nie przejdzie pod adres wysłany w wiadomości, takie konto jest 
jak konto zwykłego użytkownika.
 
 - /login - logowanie
 - /admin - dostęp do tego endpointu ma tylko użytkownik z rolą admin
 - /user - dostęp do tego endpointu mają role user i admin
 - /logout - wylogowanie


