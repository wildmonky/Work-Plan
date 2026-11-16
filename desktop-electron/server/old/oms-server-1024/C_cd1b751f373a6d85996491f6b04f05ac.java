import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_cd1b751f373a6d85996491f6b04f05ac
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:jsCommon.deleteTableData('OMS_FHDL','"+mapRow.get("ID")+"');";
return url;

    }
}
