{% extends "plan_module_parent_tpl.xml" %}
{% block name %}
        "net.echinopsii.ariane.community.core.{{module.name}}"
{% endblock %}
{% block version %}"{{version}}"{% endblock %}
{% block configuration -%}
        <artifact type="configuration" name="net.echinopsii.ariane.community.core.MappingRimManagedService"/>
        {% endblock -%}
{% block bundle -%}
    {% for s in submodules -%}
        <artifact type="bundle" name="{{ s.artifactId }}" version="[{{vmin}},{{vmax}})"/>
    {% endfor -%}
{% endblock -%}
