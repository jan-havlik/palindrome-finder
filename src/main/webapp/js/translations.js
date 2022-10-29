define("translations", function() {

    return {
        cs: {
            LENGTH: "Délka",
            CALCULATE: "Vypočítej",
            ROW: "Řádek",

            PAGE_INDEX: "Vítejte",
            PAGE_REGISTER: "Registrace",
            PAGE_PALINDROME: "Palindrom",

            MENU_ANALYSIS: "Analýzy",

            INDEX_INFO: "Program DNA analyzer slouží k hledání struktur v DNA sekvencích.",

            PALINDROME_ANALYSIS_INFO: "Zde je vidět počet opakování daného palindromu v sekvenci. Klinutím na filtr se v přehledu odfiltrují ostatní sekvence.",
            PALINDROME_GROUP_OPPOSITE: "Seskupovat i podle opačné sekvence",
            PALINDROME_GROUP_SPACER: "Seskupovat i podle spaceru"
        },
        en: {
            //caste nazvy
            TITLE: "Title",
            FORMAT: "Format",
            FILE: "File",
            UPLOAD: "Upload",
            CHROMOSOME: "Chromosome",
            SEQUENCE: "Sequence",
            SEQUENCES: "Sequences",
            GENOME: "Genome",
            LENGTH: "Length",
            SIZE: "Size",
            RANGE: "range",
            SPACER: "Spacer",
            CALCULATE: "Calculate",
            ANALYSE: "Analyse",
            SHOW_DETAILS: "Show details",
            HIDE_DETAILS: "Hide details",
            DETAILS: "Details",
            REQUEST_DURATION: "Request duration",
            FILTER: "Filter",
            CANCEL_FILTER: "Cancel filter",
            GENES: "genes",
            BASE_PAIRS: "bp",
            REFRESH: "Refresh",
            USERS: "Users",
            EMAIL: "Email",
            PASSWORD: "Password",
            UPLOADED: "Uploaded",
            DELETE: "Delete",
            TOGGLE_SEL: "Toggle selection",
            NOTHING_HERE: "Nothing to display...",
            DATE: "Date",
            PROCESSING: "Processing",
            OPTIONS: "Options",
            BACK: "Back",
            PER_SEGMENT: "per segment",
            HIGHLIGHT: "Highlight",
            SETTINGS: "Settings",
            POSITION: "Position",
            ROW: "Row",

            //modalni okna
            CONFIRM: "Confirm",
            CONFIRM_DELETE_SEL: "Do you confirm deletion of selected items?",
            YES: "Yes",
            NO: "No",

            //chyby
            ERR: "Error",
            ERR_FILL_ALL: "Fill all required fields.",
            ERR_SELECT_FILE: "Select a file.",
            ERR_EMAIL: "Email is not formatted correctly.",
            ERR_PASSWORD: "Password must have at least 5 characters.",
            ERR_PASS_MATCH: "Passwords does not match.",
            ERR_NOT_FOUND: "Not found.",
            ERR_RETYPE: "Retype correct code.",
            ERR_RANGE: "Range has wront input format.",
            ERR_PALINDROME_MISMATCH_SIZE: "Too many mismatches for this palindrome size (MinPalindromeSize = 2 * MaxMismatchCount).",

            ALREADY_LOGGED_IN: "You are already logged in.",

            PAGE_INDEX: "Welcome",
            PAGE_REGISTER: "Register",
            PAGE_LOGIN: "Login",
            PAGE_UPLOAD: "Upload sequence",
            PAGE_PREDICTOR: "p53 binding predictor",
            PAGE_PALINDROME: "Palindrome analysis",
            PAGE_LOGOUT: "Logout",
            PAGE_GENOMS_OVERVIEW: "My sequences",
            PAGE_NCBI: "NCBI import",
            PAGE_ANALYSIS_OVERVIEW: "My analysis",
            PAGE_ABOUT: "About",
            PAGE_IMPORT_UPLOAD: "NCBI import and upload",
            PAGE_FAQ: "FAQ",
            PAGE_CHANGE_PASS: "Change password",
            PAGE_RESET_PASS: "Reset password",
            PAGE_IMPORT: "Import sequences",
            PAGE_TEXT_IMPORT: "Import plain text",

            MENU_ANALYSIS: "Analysis",
            MENU_GENOMS: "Sequences",
            MENU_HELP: "Help and FAQ",

            //stranka upload a NCBI - import
            IMPORT_INFO: "On this page you can import files using direct upload or NCBI import.",
            UPLOAD_FILE_ADDED: "File uploaded.",
            UPLOAD_UPLOADING_FILE: "Uploading file.",
            UPLOAD_FAILED: "File upload failed: ",
            NO_FILE: "No file selected.",
            NCBI_IMPORT: "Import from NCBI",
            NCBI_IMPORT_DONE: "Import done",
            NCBI_ID: "NCBI ID",
            TEXT_IMPORT_DONE: "Text uploaded",

            //stranka index
            INDEX_INFO: "DNA Analyser is a web-based server for nucleotide sequence analysis. It has been developed thanks to cooperation of Department of Informatics, Mendel’s University in Brno and Institute of Biophysics, Academy of Sciences of the Czech Republic. The aim of this service is to provide a free and user-friendly tool for studying DNA features with a focus on DNA local structures. The “Palindrome analyser”, which is capable to gather information about the presence of inverted repeats in DNA sequences, was established as a first tool. Our unique application displays and analyses detailed information about different parameters of this important DNA feature including inverted repeats similarity, visualization and localization, just to mention a few.",

            //stranka palindrome
            PALINDROME: "Palindrome",
            PALINDROME_MISMATCHES: "Mismatches",
            PALINDROME_SEQUENCE: "Sequence",
            PALINDROME_SEQUENCE_FILTER: "Filter this sequence",
            PALINDROME_OPPOSITE: "Opposite",
            PALINDROME_ENERGY: "ΔG(cf) - ΔG(lin)",
            PALINDROME_ENERGY_LINEAR: "ΔG(lin)",
            PALINDROME_ENERGY_CRUCIFORM: "ΔG(cf)",
            PALINDROME_POSITION: "Position",
            PALINDROME_AS_TEXT: "Input sequence as text",
            PALINDROME_AS_NCBI: "Input NCBI ID",
            PALINDROME_CYCLYC: "Circular",
            PALINDROME_HEATMAT: "Sequence heatmap",
            PALINDROME_OVERVIEW: "Overview of palindromes",
            PALINDROME_ANALYSIS: "Analysis of similarity",
            PALINDROME_SUMMARY: "Summary",
            PALINDROME_SIZE: "Size of sequence",
            PALINDROME_COUNT: "Found # of palindromes",
            PALINDROME_BY_LENGTH: "Amounts by length",
            PALINDROME_BY_SPACER: "Amounts by spacer",
            PALINDROME_BY_MISMATCHES: "Amounts by mismatches",
            PALINDROME_AMOUNT: "Amount",
            PALINDROME_ALL_SIZES: "All sizes",
            PALINDROME_ANALYSIS_INFO: "This table shows amount of occurences for each palindrome in sequence, click the palindrome to filter other palindromes in overwiew tab.",
            PALINDROME_GROUP_SEQUENCE: "Group by sequence",
            PALINDROME_GROUP_SEQUENCE_INFO: "If checked, palindromes will be grouped by sequence.",
            PALINDROME_GROUP_OPPOSITE: "Group by opposite",
            PALINDROME_GROUP_OPPOSITE_INFO: "If checked, palindromes will be grouped by opposite sequence.",
            PALINDROME_GROUP_SPACER: "Group by spacer",
            PALINDROME_GROUP_SPACER_INFO: "If checked, palindromes will be grouped by spacer sequence.",
            PALINDROME_HIGHLIGHT_LOCATION: "Highlight location",
            PALINDROME_MIN_AMOUNT: "Minimal amount",
            PALINDROME_CANCEL_HIGHLIGHT: "Cancel highlight",
            PALINDROME_STORE_RESULT: "Store results",
            PALINDROME_FILTER: "Set this sequence as filter",
            PALINDROME_UPLOADED: "My sequences",
            PALINDROME_DINUCLEOTIDE: "Filter ATATAT...",

            LOGIN: "Login",

            REGISTER: "Register",
            REGISTER_DONE: "User registered.",
            REGISTER_RETYPE: "Retype verification code",

            GEN_OVERVIEW_MAKE: "Upload or import some sequences",

            ANALYSIS_OVERVIEW_MAKE: "Store some analysis results in palindrome analysis tool.",

            EXPORT_CSV: "CSV",
            EXPORT_CSV_HEADER: "CSV summary",

            RESET_PASS: "Reset password",
            RESET_PASS_DONE: "New password send to given email.",

            CHANGE_PASS: "Change password",
            CHANGE_PASS_DONE: "Password changed.",
            CHANGE_PASS_NEW: "New password",
            CHANGE_PASS_REPEAT: "Retype new password",
            CHANGE_PASS_OLD: "Current password",

            PREDICTOR_AMOUNT: "Show best # matches",
            PREDICTOR_SINGLE: "Single",
            PREDICTOR_MULTIPLE: "Multiple",
            PREDICTOR_FIND_BEST: "Find best",
            PREDICTOR_MULTIPLE_INPUT: "Insert sequences (one 20 base sequence per line)",
            PREDICTOR_FIND_INPUT: "Insert a sequence"
        }
    };

});