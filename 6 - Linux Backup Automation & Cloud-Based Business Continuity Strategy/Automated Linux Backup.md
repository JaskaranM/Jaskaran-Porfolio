# Systems Administration & Business Continuity Infrastructure

---

## Overview
This project presents an enterprise-grade systems infrastructure, automated maintenance, and Business Continuity Framework, designed for a fictional, data-driven market research company, NanoTech.

The implementation combines hands-on Bash automation for server maintenance and disaster recovery with a formal Business Continuity Plan (BCP) structured around ISO 22301 and NIST Cloud Computing (SP 800-145) standards.

> [!NOTE]
> **Core Objective:** Establish high availability, automate routine tasks, enforce a strict 3-2-1 backup strategy, and optimise system Recovery Time Objectives (RTO) and Recovery Point Objectives (RPO) across all business assets.

---

## Architecture & Tech Stack

```text
                           +----------------------------------+
                           |       Linux Server System        |
                           +----------------------------------+
                                            |
                    +-----------------------+-----------------------+
                    |                                               |
        +-----------------------+                       +-----------------------+
        |   Cron Scheduler      |                       |  System Monitoring   |
        +-----------------------+                       +-----------------------+
                    |                                               |
        +-----------------------+                       +-----------------------+
        | JaskaranMahal-Backup  |                       | JaskaranMahal-Disk    |
        |      (Script)         |                       |   Monitor (Script)    |
        +-----------------------+                       +-----------------------+
                    |                                               |
        +-----------------------+                       +-----------------------+
        | Full & Incremental    |                       | Emergency Backups &   |
        |     Tar Archives      |                       | Cleanup Tasks         |
        +-----------------------+                       +-----------------------+

```

## Technical Implementation: Automation Scripts

The core infrastructure automation relies on two custom Bash scripts executed via `cron` jobs to ensure system resilience, low Recovery Point Objectives (RPO), and dynamic storage cleanup.

---

### 1. Automated Backup System (`JaskaranMahal-Backup.sh`)

The backup script automates both full system snapshots and differential incremental backups using `tar` archives with custom timestamping. 

> [!NOTE]
> **Backup Strategy & RPO Optimisation:**
> * **Full Backups:** Scheduled every Sunday at **1:00 AM** to minimise server performance impact during business hours.
> * **Incremental Backups:** Scheduled every **6 hours** daily to enforce low RPO compliance for high-priority data assets.

> [!TIP]
> Incremental backups utilise GNU `tar` differential tracking (`-N '1 day'`) to capture changed files, keeping storage consumption low without compromising system recoverability.

#### Script Source Code:
```bash
#!/bin/bash

function backup(){

n=$#

if [ $n -lt 4 ] ; then
    echo "Need four arguments - [full | inc], backup_src, backup_dest and backup_name"
    exit 1
fi

backup_type=$1
backup_src=$2
tstamp=`date +%y%m%d-%H%M%S`
backup_dest=$3
backup_name=$4

mkdir -p "$backup_dest"

if [ "$backup_type" == "full" ]; then
    echo "Full Backup..."
    tar -cvf "${backup_dest}/${backup_name}_full_${tstamp}.tar" "$backup_src"

elif [ "$backup_type" == "inc" ]; then
    echo "Incremental Backup..."
    tar -cvf "${backup_dest}/${backup_name}_inc_${tstamp}.tar" -N '1 day' "$backup_src"
else
    echo "Invalid backup type, use full or inc"
    exit 1
fi

if [ $? -eq 0 ]; then
    echo "Successful archive as ${backup_name}_${backup_type}_${tstamp}.tar"
fi
}

backup "$@"

```
### Validation 


<br><br>
    
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/backup1.png">
</div>

 <br><br>


Image of the backup script in execution, listing the files backed up and the name of the backup which specifies the type of backup and the timestamp.


<br><br>
    
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/proof1.png">
</div>

Image shows the script's execution under processes and the backup files created

<br><br>




### 2. Disk Monitoring & Emergency Maintenance (`JaskaranMahal-DiskMonitor.sh`)

The monitoring script continuously inspects storage partition usage. If system storage exceeds a defined threshold percentage, it automatically triggers an emergency full backup prior to executing automated maintenance.

> [!NOTE]
> **Data Integrity Guardrail:** The script safely executes an emergency full backup before purging old files or removing unused packages, preventing accidental data loss.

#### Script Source Code:
```bash
#!/bin/bash

function disk_monitor() {

partition=$1
threshold=$2
path=$3
action=$4

n=$#

if [ $n -lt 3 ]; then
    echo "List partition, threshold, path and action (pkg or files)"
    exit 1
fi

usage=$(df \vert{} grep "$partition" | awk '{print $5}' | cut -d% -f1)
echo "Disk partition: ${usage}\% (Threshold${threshold}%) "

if [ "$usage" -gt "$threshold" ]; then
    echo "Threshold exceeded, triggering backup"
    ./JaskaranMahal-Backup.sh "full" "$path" "/home/jaz/Backups" "emergency_backup"

    if [ "$action" == "pkg" ]; then
        echo "Removing unused packages"
        sudo apt autoremove -y

    elif [ "$action" == "files" ]; then
        echo "Deleting old files"
        find "$path" -type f -mtime +1095 -delete

    else
        echo "Invalid option, cancelling operation"
    fi

else
    echo "Disk usage within limits"
fi
}

disk_monitor "$@"


```

---

## Verification

<br><br>
    
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/cron1.png">
</div>

<br><br>


Image of the cron implementation, set to execute a full backup every Sunday at 1am and an incremental backup every 6 hours".


<br><br>
    
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/proof2.png">
</div>

<br><br>

Image of the emergency backup being created after the threshold was exceeded. It also removes files older than3 years old to free up space. There is no risk of losing the old files as the emergency backup acts as a recovery point.

## Cron Scheduling

To maintain systemic resilience without disrupting business productivity, cron jobs are scheduled for automated background maintenance.

> [!IMPORTANT]
> **Execution Logic:**
> * **Incremental Backups:** `0 */6 * * *` runs every 6 hours daily to maintain a low Recovery Point Objective (RPO).
> * **Full Backups:** `0 1 * * 0` runs every Sunday at 1:00 AM to avoid impacting server performance or employee productivity during business hours.
> * **Disk Monitoring:** `0 * * * *` runs hourly checks against target storage partitions using an 80% capacity threshold.

#### Scheduled Crontab Configuration:
```cron
# Run incremental backups every 6 hours
0 */6 * * * /home/jaz/Desktop/JaskaranMahal-Backup.sh "inc" "/home/jaz/Desktop" "/home/jaz/Backups" "Incremental_Backup"

# Run weekly full backups every Sunday at 01:00 AM
0 1 * * 0 /home/jaz/Desktop/JaskaranMahal-Backup.sh "full" "/home/jaz/Desktop" "/home/jaz/Backups" "Weekly_Backup"

# Run hourly disk monitoring on /dev/sda2 with an 80% threshold
0 * * * * /home/jaz/Desktop/JaskaranMahal-DiskMonitor.sh "/dev/sda2" 80 "/home/jaz/Desktop" "files"

```

## Verification

<br><br>
    
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/cron1.png">
</div>

<br><br>


Image of the cron implementation, set to execute a full backup every Sunday at 1am and an incremental backup every 6 hours


<br><br>
    
<div align="center">
  <img src="https://github.com/JaskaranM/Jaskaran-Porfolio/blob/main/images/cron2.png">
</div>

<br><br>


Image of the cron implementation, set to execute every hour that then triggers an emergency backup when the threshold of 80% is exceeded



## Business Continuity Management (BCM) Strategy

### Current Infrastructure Resilience Strategy

1. **Database Server (Critical):** Implementation of a RAID 10 array for optimised read/write rates and rapid rebuild times compared to RAID 5 configurations.
2. **Domain Controller (Critical):** Configured with RAID 1 mirroring to eliminate single-point-of-failure risks regarding authentication.
3. **Storage & File System:** Deployment of Ext4 with journaling enabled for fast error recovery, alongside scheduled backing up of file inodes containing ownership and permission data.
4. **Network & Power:** Deployment of Uninterruptible Power Supplies (UPS) and transition to Gigabit LAN to accelerate incremental backups and dynamic system restores.
5. **3-2-1 Rule Strategy:** Maintenance of 3 data copies on 2 different media formats, with 1 secure off-site copy.

---

### Asset Recovery Matrix

| Asset | Priority Level | Recommendation | Target RTO |
| :--- | :--- | :--- | :--- |
| Database Server | Critical | RAID 10 array implemented, Automated snapshots every 15 minutes | < 1 hour |
| Domain Controller | Critical | RAID 1 array implemented, Daily backups | < 1 hour |
| Website | Critical | Move from ISP to VPS, Automated offsite backups | < 1 hour |
| Network / LAN | Critical | Install UPS, Procure redundant equipment, Upgrade to gigabit | < 1 hour |
| File Server | Important | RAID 10 array implemented, Ext4 journaling, 3-2-1 backup rule implemented | 2–6 hours |
| Client PCs | Important | Use disk imaging (Golden Image) | 2–6 hours |
| Email Server | Normal | Incremental backups every 6 hours | 24+ hours |
| Backup Server | Normal | RAID 1 array implemented | 24+ hours |

---

## Cloud Transformation Strategy

Following the NIST Cloud Computing Reference Architecture (SP 800-145)**, NanoTech's infrastructure can be modernized by adopting cloud deployment models[cite: 1]:

* IaaS (Infrastructure as a Service): Solves physical server bottlenecks and bypasses single-file limits (16TB on Ext4), introducing automated auto-scaling during high-demand workloads.
* SaaS (Software as a Service): Transitioning email and productivity suites to Microsoft 365 delivers a near-zero RTO, 99.9%+ SLA uptime, and secure remote working capability.
* PaaS (Platform as a Service): Moving web operations to services like AWS Elastic Beanstalk grants developers full deployment control and security integration without managing the underlying OS.

## Conclusion
This project taught me how to think as a system administration, automating tasks to improve RTO and RPO times in order to minimise the risk of data loss. The second half pushed me to look at the current infrastructure and to determine weakpoints, flaws, bottlenecks and to then provide a strategy and suggestions to minimise the impact a disaster would have on the business.
