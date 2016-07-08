# Contributing to the IBM Watson&trade; Conversation Sample App

We would love for you to contribute to IBM Watson&trade; Conversation Sample App and help make it even
better than it is today! As a contributor, here are the guidelines we would like you to follow:

 - [Question or Problem?](#question)
 - [Issues and Bugs](#issue)
 - [Feature Requests](#feature)
 - [Submission Guidelines](#submit)
 - [Coding Rules](#rules)
 - [Commit Message Guidelines](#commit)

## <a name="question"></a> Got a Question or Problem?

If you have questions about how to *use* the tool or the service, please post them on [dW Answers and tag them with
`watson-conversation`][dWanswers].

## <a name="issue"></a> Found an Issue?
If you find a bug or a mistake in the documentation, you can help us by [submitting an issue](#submit-issue) in our [product backlog][weabacklog] and label it as a *bug*. Even better, you can
[submit a Pull Request](#submit-pr) with a fix.

The issues in this repository are bugs that have been triaged, or tasks that have been prioritised by offering management for the development team. In general, new issues should go to the [product backlog][weabacklog] first.

## <a name="feature"></a> Want a Feature?
You can *request* a new feature by [submitting an issue](#submit-issue). If you would like to *implement* a new feature, please submit an issue with
a proposal for your work first, to be sure that we can use it.
Please consider what kind of change it is:

* For a **Major Feature**, first open an issue and outline your proposal so that it can be
discussed. This will also allow us to better coordinate our efforts, prevent duplication of work,
and help you to craft the change so that it is successfully accepted into the project.
* **Small Features** can be crafted and directly [submitted as a Pull Request](#submit-pr).

## <a name="submit"></a> Submission Guidelines

### <a name="submit-issue"></a> Submitting an Issue
Before you submit an issue, search the archive, both in this repository and in the [backlog][weabacklog] maybe your question was already answered.

If your issue appears to be a bug, and hasn't been reported, open a new issue in our [product backlog][weabacklog].
Help us to maximize the effort we can spend fixing issues and adding new
features, by not reporting duplicate issues.  Providing the following information will increase the
chances of your issue being dealt with quickly:

* **Overview of the issue** - if an error is being thrown a non-minified stack trace helps
* **Motivation for or use case** - explain why this is a bug for you
* **Browsers and operating system** - is this a problem with all browsers?
* **Reproduce the error** - provide a copy of the data used when experience the error or an unambiguous set of steps.
* **Related issues** - has a similar issue been reported before?
* **Suggest a fix** - if you can't fix the bug yourself, perhaps you can point to what might be
  causing the problem (line of code or commit)

### <a name="submit-pr"></a> Submitting a Pull Request (PR)
Before you submit your Pull Request (PR) consider the following guidelines:

* Search [GitHub][pullrequests] for an open or closed PR
  that relates to your submission. You don't want to duplicate effort.
* Fork the repository into your own space. [Forking a repo][fork]
* Set up the base repository as a remote called `upstream`:

  ```shell
  git remote add upstream git@github.ibm.com/watson-engagement-advisor/wea-app.git
  ```

* Create a new git branch off of `develop`:

  ```shell
  git checkout -b my-fix-branch develop
  ```

* Make your changes, **including appropriate test cases**. Added code should have 100% test coverage for statements, branches, functions, and lines.
* Follow our [Coding Rules](#rules).
* Run a full build, including linters, unit tests and coverage reports, using the `gulp` command and verify that all tests pass.
* Commit your changes using a descriptive commit message that follows our
  [commit message conventions](#commit). Adherence to these conventions
  is necessary because release notes are automatically generated from these messages.

  ```shell
  git commit -a
  ```
  Note: the optional commit `-a` command line option will automatically "add" and "rm" edited files.

* If you have made multiple commits, squash your changes into one commit (unless it is important to separate the changes):

  ```shell
  git rebase -i HEAD~#
  ```

  Note: Replace `#` with the number of commits on the branch. This will enter an interactive mode which will allow you to squash your commits into one. See [squashing][squashing] to learn more.

* Ensure that your branch is based on the latest from the base repository:

  ```shell
  git fetch upstream
  git rebase upstream/develop
  ```

  Note: the second command is only necessary if the first command retrieves an update on the `develop` branch.

* Push your branch to GitHub:

  ```shell
  git push -u origin my-fix-branch
  ```

  Note: the optional `-u` flag enables tracking of your local branch with the new remote branch.

* In GitHub, send a pull request to `wea-app:develop`.
* If we suggest changes then:
  * Make the required updates and commit them.
  * Re-run the test suite to ensure tests are still passing.
  * Squash your commits into one and force push to your GitHub repository (this will update your Pull Request):

  ```shell
  git rebase -i HEAD~#
  git push -f
  ```

  Note: See above or [squashing][squashing] for more information on how to squash interactively.

That's it! Thank you for your contribution!

#### After your pull request is merged

After your pull request is merged, you can safely delete your branch and pull the changes
from the main (upstream) repository:

* Delete the remote branch on GitHub either through the GitHub web UI or your local shell as follows:

    ```shell
    git push origin --delete my-fix-branch
    ```

* Check out the `develop` branch:

    ```shell
    git checkout develop -f
    ```

* Delete the local branch:

    ```shell
    git branch -D my-fix-branch
    ```

* Update your `develop` with the latest upstream version:

    ```shell
    git pull --ff upstream develop
    ```

## <a name="rules"></a> Coding Rules
To ensure consistency throughout the source code, keep these rules in mind as you are working:

* All features or bug fixes **must be tested** by one or more specs (unit-tests).
* Your code should be happy when run through `gulp lint:scripts lint:tests`.
* While we don't have our own formal style guide yet, please to try to be consistent with the rest of the code.
* [Watson Developer Cloud API guidelines](https://github.com/watson-developer-cloud/api-guidelines) provides a reference point for general practices and also provides a Java style guide.
* [Codestyle.co AngularJS Style Guide](http://www.codestyle.co/Guidelines/angularjs) is a good reference for current leading practice.
* If in doubt, please ask.

## <a name="commit"></a> Commit Message Guidelines

We have very precise rules over how our git commit messages can be formatted.  This leads to more
readable messages that are easy to follow when looking through the project history.  But also,
we use the git commit messages to generate the change log for each new release.

### Commit Message Format
Each commit message consists of a **header**, a **body** and a **footer**.  The header has a special
format that includes a **type**, a **scope** and a **subject**:

```
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

The **header** is mandatory and the **scope** of the header is optional.

Any line of the commit message cannot be longer 80 characters. This allows the message to be easier
to read on GitHub as well as in various git tools.

### Revert
If the commit reverts a previous commit, it should begin with `revert: `, followed by the header of the reverted commit.
In the body it should say: `This reverts commit <hash>.`, where the hash is the SHA of the commit being reverted.

### Type
Must be one of the following:

* **feat**: A new feature
* **fix**: A bug fix
* **docs**: Documentation only changes
* **style**: Changes that do not affect the meaning of the code (white-space, formatting, missing
  semi-colons, etc)
* **refactor**: A code change that neither fixes a bug nor adds a feature
* **perf**: A code change that improves performance
* **test**: Adding missing tests
* **chore**: Changes to the build process or auxiliary tools and libraries such as documentation
  generation

### Scope
The scope could be anything specifying place of the commit change. For example
`Ranker`, `Setup`, etc.

### Subject
The subject contains succinct description of the change:

* Use the present tense ("Add server side CSV export" vs "Added server side CSV export")
* Use the imperative ("Add server side CSV export" vs "Add CSV export to server")
* Capitalize the first word in your commit after the <type>(<scope>): identifier
* A title is not a sentence – don't close it with a period
* Assume that your commit message will be rendered as plain text - don't use markdown

Where possible the subject should be under 50 characters. This isn't a hard and fast rule, but it's a general
guideline that allows support for as many tools as possible.

### Body
Just as in the **subject**, use the imperative, present tense: "change" not "changed" nor "changes".
The body should include the motivation for the change and contrast this with previous behavior.

### Footer
The footer should contain any information about **Breaking Changes** and is also the place to
reference GitHub issues that this commit **Closes**. If you're working on a larger issue that
spans multiple commits it's safe to refer to the issue in the footer, but you may want to squash
the history before submitting your pull request.

**Breaking Changes** should start with the word `BREAKING CHANGE:` with a space or two newlines. The rest of the commit
message is then used for this.

A detailed explanation can be found in the [AngularJS Git Commit Message Conventions][commit-message-format], which
we largely inherit.

### Developer Certificate of Origin

All contributions to the IBM Watson&trade; Conversation Service Sample App must be accompanied by acknowledgment of,
and agreement to, the [Developer Certificate of Origin](http://elinux.org/Developer_Certificate_Of_Origin), reproduced
below. Acknowledgment of and agreement to the Developer Certificate of Origin _must_ be included in the comment section
of each contribution and _must_ take the form of `DCO 1.1 Signed-off-by: {{Full Name}} <{{email address}}>`
(without the `{}`). Contributions without this acknowledgment will be required to add it before being accepted. If a
contributor is unable or unwilling to agree to the Developer Certificate of Origin, their contribution will not be
included.

```
Developer Certificate of Origin
Version 1.1

Copyright (C) 2004, 2006 The Linux Foundation and its contributors.
660 York Street, Suite 102,
San Francisco, CA 94110 USA

Everyone is permitted to copy and distribute verbatim copies of this
license document, but changing it is not allowed.

Developer's Certificate of Origin 1.1

By making a contribution to this project, I certify that:

(a) The contribution was created in whole or in part by me and I
    have the right to submit it under the open source license
    indicated in the file; or

(b) The contribution is based upon previous work that, to the best
    of my knowledge, is covered under an appropriate open source
    license and I have the right under that license to submit that
    work with modifications, whether created in whole or in part
    by me, under the same open source license (unless I am
    permitted to submit under a different license), as indicated
    in the file; or

(c) The contribution was provided directly to me by some other
    person who certified (a), (b) or (c) and I have not modified
    it.

(d) I understand and agree that this project and the contribution
    are public and that a record of the contribution (including all
    personal information I submit with it, including my sign-off) is
    maintained indefinitely and may be redistributed consistent with
    this project or the open source license(s) involved.
```

### Example Commit Message

Here's a sample commit message that adheres to our commit message guidelines:

```
feat(import): Add support for XML training data

Integrate support for the new simplified XML training data format that
allows data interchange with System X and System Z. While the format is
slightly more verbose than the CSV format, the interchange should help
speed adoption.

For more information see http://some.url/

Closes #7

DCO 1.1 Signed-off-by: Patrick Wagstrom <pwagstro@us.ibm.com>
```

## <a name="credits"></a> Credits

This document is based off the contributing standards from [Angular][angularcontributing]. We are thankful for their
work in setting up good standards and norms of software development.

[commit-message-format]: https://docs.google.com/document/d/1QrDFcIiPjSLDn3EL15IJygNPiHORgU1_OOAqWjiDU5Y/edit#
[github]: https://github.ibm.com/watson-engagement-advisor/wea-app
[weabacklog]: https://github.ibm.com/watson-engagement-advisor/wea-backlog
[pullrequests]: https://github.ibm.com/watson-engagement-advisor/wea-app/pulls
[angularcontributing]: https://github.com/angular/angular/blob/master/CONTRIBUTING.md
[dwAnswers]: https://developer.ibm.com/answers/topics/watson-conversation/
[fork]: https://help.github.com/articles/fork-a-repo/
[squashing]: https://git-scm.com/book/en/v2/Git-Tools-Rewriting-History#Changing-Multiple-Commit-Messages
