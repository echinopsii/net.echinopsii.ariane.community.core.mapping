{% extends "plan_module_parent_tpl.xml" %}
{% block name %}
    {%- if module.type == 'none' -%}
        "net.echinopsii.ariane.community.{{module.name}}."
    {%- else -%}
        "net.echinopsii.ariane.community.core.{{module.name}}"
    {%- endif -%}
{% endblock %}
{% block version %}"{{version}}"{% endblock %}
{%- block configuration -%}
        <artifact type="configuration" name="net.echinopsii.ariane.community.core.MappingRimManagedService"/>
        {%- endblock -%}
{% block bundle -%}
    {% for s in submodules -%}
    {%- if s.name != 'dsl' -%}
        <artifact type="bundle" name="{{ s.artifactId }}" version="[{{vmin}},{{vmax}})"/>
    {% endif -%}
    {%- endfor -%}
{% endblock %}
