import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_d0691b7f661c2252a53302f72e1c89b7
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:jsCommon.deleteTableData('SYS_FLOW_INSTANCE','"+mapRow.get("FLOW_INSTANCE_ID")+"','W_MXZCYXFS','"+mapRow.get("ID")+"');";
return url;

    }
}
