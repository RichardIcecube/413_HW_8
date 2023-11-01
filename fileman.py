import os
import shutil


fileName1 = input("Enter the first filename: ")
fileName2 = input("Enter the second filename: ")

os.system(f"diff -U 0 {fileName1} {fileName2} > differences.txt")

os.makedirs('old', exist_ok=True)
os.makedirs('new', exist_ok=True)
os.makedirs('changesets', exist_ok=True)

shutil.copy(fileName1, 'old/')
shutil.copy('old/file1v1.java', 'old/file1v1_orig.java')
shutil.copy(fileName2, 'new/')

with open('differences.txt', 'r') as f:
    lines = f.readlines()

changeset_num = 0
changeset = []
file1 = ''
file2 = ''
isOpen = False
for line in lines:
    if line.startswith('---'):
        file1 = line
    if line.startswith('+++'):
        file2 = line
    if line.startswith('@@'):
        if isOpen:
           if changeset:  # If there's already a changeset, write it to a file
                with open(f'changesets/changeset{changeset_num}.txt', 'w') as f:
                    f.write(''.join(changeset))
                changeset_num += 1
                changeset = []
                isOpen = False 
        if not isOpen:
            changeset.append(file1)
            changeset.append(file2)
            changeset.append(line)
            isOpen = True
            
    if line.startswith('+ ') or line.startswith('- '):
        changeset.append(line)

# Don't forget the last changeset
if changeset:
    with open(f'changesets/changeset{changeset_num}.txt', 'w') as f:
        f.write(''.join(changeset))