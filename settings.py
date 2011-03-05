# Django settings for code_project project.
import sys, os

def rel(*x):
    return os.path.join(os.path.abspath(os.path.dirname(__file__)), *x)

def rel_to(to, *x):
    return os.path.join(to, *x)

DISTR_DIR='/home/german/distr'
LIB_DIR='/home/german/work/libs'

DEBUG = True
TEMPLATE_DEBUG = DEBUG


ADMINS = (
    # ('Your Name', 'your_email@domain.com'),
)

MANAGERS = ADMINS

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql', # Add 'postgresql_psycopg2', 'postgresql', 'mysql', 'sqlite3' or 'oracle'.
        'NAME': 'watchme',                      # Or path to database file if using sqlite3.
        'USER': 'watchme',                      # Not used with sqlite3.
        'PASSWORD': 'watchme',                  # Not used with sqlite3.
        'HOST': '',                      # Set to empty string for localhost. Not used with sqlite3.
        'PORT': '',                      # Set to empty string for default. Not used with sqlite3.
    }
}

# Local time zone for this installation. Choices can be found here:
# http://en.wikipedia.org/wiki/List_of_tz_zones_by_name
# although not all choices may be available on all operating systems.
# On Unix systems, a value of None will cause Django to use the same
# timezone as the operating system.
# If running in a Windows environment this must be set to the same as your
# system time zone.
TIME_ZONE = 'Asia/Almaty'

# Language code for this installation. All choices can be found here:
# http://www.i18nguy.com/unicode/language-identifiers.html
LANGUAGE_CODE = 'ru-RU'
LANGUAGES = (
    ("ru","Russian"),
    ("en","English"),
#    ("kz","Kazakh"),
#    ("tr","Turkish"),
)

SITE_ID = 1

# If you set this to False, Django will make some optimizations so as not
# to load the internationalization machinery.
USE_I18N = True

# If you set this to False, Django will not format dates, numbers and
# calendars according to the current locale
USE_L10N = True

# Absolute path to the directory that holds media.
# Example: "/home/media/media.lawrence.com/"
MEDIA_ROOT = '/home/german/work/media/watchme/'

# URL that handles the media served from MEDIA_ROOT. Make sure to use a
# trailing slash if there is a path component (optional in other cases).
# Examples: "http://media.lawrence.com", "http://example.com/media/"
MEDIA_URL = '/media/'

# URL prefix for admin media -- CSS, JavaScript and images. Make sure to use a
# trailing slash.
# Examples: "http://foo.com/media/", "/media/".
ADMIN_MEDIA_PREFIX = '/media/admin/'

# Make this unique, and don't share it with anybody.
SECRET_KEY = 'x7+2eb0f2zd_to48j11jrrk*j*bm_4k$%w%(%h0_1f-33mdrd*'

#email stuff
EMAIL_HOST = 'smtp.gmail.com'
EMAIL_HOST_USER = 'watchme.kz@gmail.com'
EMAIL_HOST_PASSWORD = 'damnsimple'
EMAIL_USE_TLS = True
EMAIL_PORT = 587
EMAIL_SUBJECT_PREFIX = '[watchme.kz] '
SERVER_EMAIL = 'no-reply@watchme.kz'
DEFAULT_FROM_EMAIL = 'no-reply@watchme.kz'

# List of callables that know how to import templates from various sources.
TEMPLATE_LOADERS = (
    'django.template.loaders.filesystem.Loader',
    'django.template.loaders.app_directories.Loader',
    'django.template.loaders.app_directories.load_template_source'
#     'django.template.loaders.eggs.Loader',
)

TEMPLATE_CONTEXT_PROCESSORS = (
"django.contrib.auth.context_processors.auth",
"django.core.context_processors.debug",
"django.core.context_processors.i18n",
"django.core.context_processors.media",
"django.contrib.messages.context_processors.messages",
'django.core.context_processors.i18n',
)

MIDDLEWARE_CLASSES = (
    'django.middleware.common.CommonMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'sso.middleware.SingleSignOnMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.middleware.locale.LocaleMiddleware',
    'common.middleware.UserLanguageMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.contrib.flatpages.middleware.FlatpageFallbackMiddleware',
)

ROOT_URLCONF = 'watchme.urls'

TEMPLATE_DIRS = (
    rel('templates'),
)

#ROBOTS_SITEMAP_URLS = ['http://www.buben.kz/sitemap.xml']


INSTALLED_APPS = (
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.sites',
    'django.contrib.messages',
    'django.contrib.markup',
    'django.contrib.flatpages',
    'django.contrib.admin',
    'django.contrib.sitemaps',

#    'django-backup',
#    'tagging',
    'south',
    'sorl.thumbnail',
#    'basic.inlines',
#    'photologue',
#    'haystack',
    'robots',
    'uni_form',
    'registration',
    'invitation',
    
    'jobapp',
    'common',
    'sso',
)

try:
    from local_settings import *
except ImportError:
    pass

sys.path.append(os.path.join(DISTR_DIR,"django-trunk"))
sys.path.append(LIB_DIR)
sys.path.append(rel('apps'))

THUMBNAIL_EXTENSION = 'png'
THUMBNAIL_DEBUG = DEBUG

#invitation
INVITE_MODE = True
ACCOUNT_INVITATION_DAYS = 15
INVITATIONS_PER_USER = 50

# we need this for registration stuff
REQUIRE_EMAIL_CONFIRMATION = True
ACCOUNT_ACTIVATION_DAYS = ACCOUNT_INVITATION_DAYS
LOGIN_REDIRECT_URL = '/ja/daily/'
LOGIN_URL = '/accounts/login/'
