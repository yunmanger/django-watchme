from django.db import models
from django.contrib.auth.models import User

from django.utils.translation import ugettext_lazy as _

from sorl.thumbnail.fields import ImageWithThumbnailsField
import datetime
#PUBLICJOB_CHOICES = (
#    (0, _('for group')),
#    (1, _('friends')),
#)

GROUP_PUBLICITY = (
     (0, _('private')),
     (1, _('public')),
)

class PublicManager(models.Manager):
    
    def published(self):
        return self.get_query_set().filter(is_deleted=False)

class DailyJob(models.Model):
    user        = models.ForeignKey(User)
    title       = models.CharField(_('title'), max_length=100)
    text        = models.TextField(_('description'), max_length=1000)
    n           = models.IntegerField(_('N parts'), default=1)
    is_on       = models.BooleanField(_('is enabled'), default=True)
    is_deleted  = models.BooleanField(default=False, editable=False)
    importance  = models.IntegerField(default=1)
    #TODO: check if auto_now_add is right
    pub_date    = models.DateTimeField(auto_now_add=True)
    objects     = PublicManager()
    
    class Meta:
        ordering = ('-is_on','-importance','-pub_date','title')

    def get_today(self):
        today = datetime.datetime.today().date()
        try:
            return self.dailyjobtick_set.filter(date=today)[0]
        except:
            pass
        return None
        
    @models.permalink
    def del_link(self):
        return ('dailyjob_delete',None,{'id': self.pk})

    @models.permalink
    def toggle_link(self):
        return ('dailyjob_toggle',None,{'id': self.pk})

    @models.permalink
    def edit_link(self):
        return ('dailyjob_edit',None,{'id': self.pk})
    
    @models.permalink
    def stat_link(self):
        return ('stat_dailyjob',None,{'id': self.pk})
    
    def __unicode__(self):
        return self.title

class DailyJobTickManager(models.Manager):

    def published(self):
        return self.get_query_set().filter(job__is_deleted=False)
    
class DailyJobTick(models.Model):
    job         = models.ForeignKey(DailyJob)
    done        = models.IntegerField(default=0)
    date        = models.DateField()
    #TODO: check if auto_now_add is right
    pub_date    = models.DateTimeField(auto_now_add=True)
    objects     = DailyJobTickManager()
    
    class Meta:
        unique_together= ('job', 'date',)
        ordering = ('job', '-date')
        
    @models.permalink
    def edit_link(self):
        return ('dailyjob_done_edit',(),{'id':self.job.pk, 'tid': self.pk})
    
class JobGroup(models.Model):
    def upload_dir(self, filename):
        return "uploads/sorl/%s/%s" % (self.user.username, filename)
    
    user        = models.ForeignKey(User)
    title       = models.CharField(max_length=100)
    text        = models.TextField(max_length=2000)
#    picture     = ImageWithThumbnailsField(
#        upload_to=upload_dir,
#        thumbnail={'size': (100, 100)},
#        extra_thumbnails={
#            'icon': {'size': (64, 64), 'options': ['crop', 'upscale']},
#            'large': {'size': (200, 400)},
#        },
#    )
    #NOTE: may change later
    is_public   = models.IntegerField(default=1, choices=GROUP_PUBLICITY)
    #TODO: check if auto_now_add is right
    pub_date    = models.DateTimeField(auto_now_add=True)
    


class Job(models.Model):
    user        = models.ForeignKey(User)
    group       = models.ForeignKey(JobGroup, null=True, blank=True)
    title       = models.CharField(_('title'), max_length=100)
    text        = models.TextField(_('description'), max_length=1000)
    n           = models.IntegerField(_('N parts'), default=1)
    is_on       = models.BooleanField(_('is enabled'), default=True)
    is_done     = models.BooleanField(default=False)
    is_deleted  = models.BooleanField(default=False, editable=False)
    importance  = models.IntegerField(default=1)
    deadline    = models.DateTimeField(null=True, blank=True)
    #TODO: check if auto_now_add is right
    pub_date    = models.DateTimeField(auto_now_add=True)

#class SubJobTick(models.Model):
#    user        = models.ForeignKey(User)
#    job         = models.ForeignKey(Job)
#    start       = models.IntegerField(default=1)
#    end         = models.IntegerField(default=1)
#    done        = models.IntegerField(default=0, choices=
#                                      (0, "not done"))


#class PublicJob(models.Model):
#    user        = models.ForeignKey(User)
#    group       = models.ForeignKey('common.Group')
#    title       = models.CharField(_('title'), max_length=100)
#    text        = models.TextField(_('description'), max_length=1000)
#    n           = models.IntegerField(_('N parts'), default=1)
#    is_on       = models.BooleanField(_('is enabled'), default=True)
#    is_done     = models.BooleanField(default=False)
#    importance  = models.IntegerField(default=1)
#    deadline    = models.DateTimeField(null=True, blank=True)
#    #TODO: check if auto_now_add is right
#    pub_date    = models.DateTimeField(auto_now_add=True)
